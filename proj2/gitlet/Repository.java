package gitlet;

import java.io.File;
import java.util.*;
import java.util.List;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *
 *  does at a high level.
 *
 *  @author dazi9
 */
public class Repository {
    /**
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");


    private static String getCurrentBranch() {
        File currentBranch = join(GITLET_DIR, "currentBranch");
        return readContentsAsString(currentBranch);
    }

    private static Commit getBranchCommit(String branchName) {
        File branches = join(GITLET_DIR, "branches");
        File branchFile = join(branches, branchName);
        File commits = join(GITLET_DIR, "commits");
        File commitFile = join(commits, readContentsAsString(branchFile));
        return readObject(commitFile, Commit.class);
    }

    private static Commit getHeadCommit() {
        return getBranchCommit(getCurrentBranch());
    }

    private static Commit findSplitPoint(Commit currentCommit, Commit givenCommit) {
        Set<String> currentAncestors = new HashSet<>();
        currentAncestors.add(currentCommit.getCommitSHA1());
        Queue<String> queue = new ArrayDeque<>();
        queue.add(currentCommit.getCommitSHA1());
        File commits = join(GITLET_DIR, "commits");
        while (!queue.isEmpty()) {
            String commitID = queue.remove();
            File commitFile = join(commits, commitID);
            currentCommit = readObject(commitFile, Commit.class);
            String firstParentSHA1 = currentCommit.getFirstParentSHA1();
            String secondParentSHA1 = currentCommit.getSecondParentSHA1();
            if (firstParentSHA1 != null && currentAncestors.add(firstParentSHA1)) {
                queue.add(firstParentSHA1);
            }
            if (secondParentSHA1 != null && currentAncestors.add(secondParentSHA1)) {
                queue.add(secondParentSHA1);
            }
        }
        Commit splitCommit = null;
        Set<String> givenAncestors = new HashSet<>();
        givenAncestors.add((givenCommit.getCommitSHA1()));
        queue.add(givenCommit.getCommitSHA1());
        while (!queue.isEmpty()) {
            String commitID = queue.remove();
            File commitFile = join(commits, commitID);
            Commit currentCommitNode = readObject(commitFile, Commit.class);
            String firstParentSHA1 = currentCommitNode.getFirstParentSHA1();
            String secondParentSHA1 = currentCommitNode.getSecondParentSHA1();
            if (firstParentSHA1 != null && givenAncestors.add(firstParentSHA1)) {
                queue.add(firstParentSHA1);
            }
            if (secondParentSHA1 != null && givenAncestors.add(secondParentSHA1)) {
                queue.add(secondParentSHA1);
            }
        }
        Set<String> commonAncestor = new HashSet<>();
        for (String ancestor : givenAncestors) {
            if (currentAncestors.contains(ancestor)) {
                commonAncestor.add(ancestor);
            }
        }
        queue.add(givenCommit.getCommitSHA1());
        while (!queue.isEmpty()) {
            String commitID = queue.remove();
            File commitFile = join(commits, commitID);
            Commit commit = readObject(commitFile, Commit.class);
            if (commonAncestor.contains(commitID)) {
                splitCommit = commit;
                return splitCommit;
            } else {
                if (commit.getFirstParentSHA1() != null) {
                    queue.add(commit.getFirstParentSHA1());
                }
                if (commit.getSecondParentSHA1() != null) {
                    queue.add(commit.getSecondParentSHA1());
                }
            }
        }
        return splitCommit;
    }

    private static void mergeCommitCommand(String message,
        String currentBranchName, String givenBranchName) {
        File stagingFile = join(GITLET_DIR, "stagingFile");
        File removalFile = join(GITLET_DIR, "removalFile");
        HashSet<String> rmMap = readObject(removalFile, HashSet.class);
        HashMap<String, String> stagingMap = readObject(stagingFile, HashMap.class);
        if (stagingMap.isEmpty() && rmMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        File branches = join(GITLET_DIR, "branches");
        File currentBranch = join(branches, currentBranchName);
        File commits = join(GITLET_DIR, "commits");
        HashMap<String, String> parentMap = getHeadCommit().getMap();
        HashMap<String, String> map = new HashMap<>(parentMap);
        for (String key : stagingMap.keySet()) {
            map.put(key, stagingMap.get(key));
        }
        for (String key : rmMap) {
            map.remove(key);
        }
        rmMap.clear();
        writeObject(removalFile, rmMap);
        Commit commit = new Commit(message, readContentsAsString(currentBranch),
                getBranchCommit(givenBranchName).getCommitSHA1(), map);
        File fileName = join(commits, commit.getCommitSHA1());
        writeObject(fileName, commit);
        writeContents(currentBranch, commit.getCommitSHA1());
        HashMap<String, String> m = new HashMap<>();
        writeObject(stagingFile, m);
    }

    public static void initCommand() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdirs();
            File stagingFile = join(GITLET_DIR, "stagingFile");
            HashMap<String, String> map = new HashMap<>();
            writeObject(stagingFile, map);
            File blobs = join(GITLET_DIR, "blobs");
            blobs.mkdir();
            File commits = join(GITLET_DIR, "commits");
            commits.mkdir();
            HashMap<String, String> m = new HashMap<>();
            Commit initCommit = new Commit("initial commit", null, null, m);
            File branches = join(GITLET_DIR, "branches");
            branches.mkdir();
            File currentBranch = join(GITLET_DIR, "currentBranch");
            writeContents(currentBranch, "master");
            File currentCommit = join(branches, "master");
            writeContents(currentCommit, initCommit.getCommitSHA1());
            writeObject(join(commits, initCommit.getCommitSHA1()), initCommit);
            File removalFile = join(GITLET_DIR, "removalFile");
            HashSet<String> s = new HashSet<>();
            writeObject(removalFile, s);
        } else {
            System.out.println(
                "A Gitlet version-control system already exists in the current directory.");
        }
    }

    public static void checkInit() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static void addCommand(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists() || !file.isFile()) {
            System.out.println("File does not exist.");
            return;
        }
        File stagingFile = join(GITLET_DIR, "stagingFile");
        HashMap<String, String> map =
                (HashMap<String, String>) readObject(stagingFile, HashMap.class);
        String blobSHA1 = sha1(readContents(file));
        Commit commit = getHeadCommit();
        File removalFile = join(GITLET_DIR, "removalFile");
        HashSet<String> rm = readObject(removalFile, HashSet.class);
        if (commit.getMap().containsKey(fileName)) {
            rm.remove(fileName);
            writeObject(removalFile, rm);
            if (commit.getMap().get(fileName).equals(blobSHA1)) {
                map.remove(fileName);
                Utils.writeObject(stagingFile, map);
                return;
            } else {
                map.put(fileName, blobSHA1);
                writeObject(stagingFile, map);
            }
        } else {
            map.put(fileName, blobSHA1);
            writeObject(stagingFile, map);
        }
        File blobs = join(GITLET_DIR, "blobs");
        File blob = join(blobs, blobSHA1);
        writeContents(blob, (Object) readContents(file));
    }

    public static void commitCommand(String message) {
        File stagingFile = join(GITLET_DIR, "stagingFile");
        File removalFile = join(GITLET_DIR, "removalFile");
        HashSet<String> rmMap = readObject(removalFile, HashSet.class);
        HashMap<String, String> stagingMap = readObject(stagingFile, HashMap.class);
        if (stagingMap.isEmpty() && rmMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (message.isBlank()) {
            System.out.println("Please enter a commit message.");
            return;
        } else {
            File branches = join(GITLET_DIR, "branches");
            File currentBranch = join(branches, getCurrentBranch());
            File commits = join(GITLET_DIR, "commits");
            HashMap<String, String> parentMap = getHeadCommit().getMap();
            HashMap<String, String> map = new HashMap<>(parentMap);
            for (String key : stagingMap.keySet()) {
                map.put(key, stagingMap.get(key));
            }
            for (String key : rmMap) {
                map.remove(key);
            }
            rmMap.clear();
            writeObject(removalFile, rmMap);
            Commit commit = new Commit(message, readContentsAsString(currentBranch), null, map);
            File fileName = join(commits, commit.getCommitSHA1());
            writeObject(fileName, commit);
            writeContents(currentBranch, commit.getCommitSHA1());
            HashMap<String, String> m = new HashMap<>();
            writeObject(stagingFile, m);
        }
    }

    public static void checkoutFile(String fileName) {
        Commit commit = getHeadCommit();
        checkoutCommitFromFile(commit, fileName);
    }

    public static void checkoutCommitFile(String commitID, String fileName) {
        File commits = join(GITLET_DIR, "commits");
        List<String> commitList = plainFilenamesIn(commits);
        int commitCount = 0;
        for (String commitName : commitList) {
            if (commitName.startsWith(commitID)) {
                commitID = commitName;
                commitCount++;
            }
        }
        File commitFile = join(commits, commitID);
        if (!commitFile.exists() || commitCount >= 2) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit commit = readObject(commitFile, Commit.class);
        checkoutCommitFromFile(commit, fileName);
    }

    private static void checkoutCommitFromFile(Commit commit, String fileName) {
        File file = join(CWD, fileName);
        if (!commit.getMap().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File blobs = join(GITLET_DIR, "blobs");
        File blob = join(blobs, commit.getMap().get(fileName));
        writeContents(file, readContents(blob));
    }

    public static void checkoutBranch(String branch) {
        File branches = join(GITLET_DIR, "branches");
        File branchFile = join(branches, branch);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        File currentBranch = join(GITLET_DIR, "currentBranch");
        if (getCurrentBranch().equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Commit currentCommit = getHeadCommit();
        Commit targetCommit = getBranchCommit(branch);
        HashMap<String, String> targetMap =
                new HashMap<>(targetCommit.getMap());
        HashMap<String, String> currentMap =
                new HashMap<>(currentCommit.getMap());
        for (String fileName : targetMap.keySet()) {
            File file = join(CWD, fileName);
            if (file.exists() && !currentMap.containsKey(fileName)) {
                System.out.println(
                    "There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                return;
            }
        }
        File blobs = join(GITLET_DIR, "blobs");
        for (String fileName : currentMap.keySet()) {
            if (!targetMap.containsKey(fileName)) {
                restrictedDelete(fileName);
            }
        }
        for (String fileName : targetMap.keySet()) {
            File file = join(CWD, fileName);
            File blob = join(blobs, targetMap.get(fileName));
            writeContents(file, readContents(blob));
        }
        HashMap<String, String> m = new HashMap<>();
        File stagingFile = join(GITLET_DIR, "stagingFile");
        writeObject(stagingFile, m);
        writeContents(currentBranch, branch);
        File removalFile = join(GITLET_DIR, "removalFile");
        HashSet<String> rm = new HashSet<>();
        writeObject(removalFile, rm);
    }

    public static void logCommand() {
        File commits = join(GITLET_DIR, "commits");
        Commit commit = getHeadCommit();
        while (commit.getFirstParentSHA1() != null) {
            message("===");
            message("commit %s", commit.getCommitSHA1());
            if (commit.getSecondParentSHA1() != null) {
                message("Merge: " + commit.getFirstParentSHA1().substring(0, 7)
                        + " " + commit.getSecondParentSHA1().substring(0, 7));
            }
            String date = String.format(Locale.US,
                    "%ta %tb %te %tT %tY %tz",
                    commit.getDate(),
                    commit.getDate(),
                    commit.getDate(),
                    commit.getDate(),
                    commit.getDate(),
                    commit.getDate());
            message("Date: %s", date);
            message("%s", commit.getMessage());
            message("");
            File parentCommitFile = join(commits, commit.getFirstParentSHA1());
            commit = readObject(parentCommitFile, Commit.class);
        }
        message("===");
        message("commit %s", commit.getCommitSHA1());
        String date = String.format(Locale.US,
                "%ta %tb %te %tT %tY %tz",
                commit.getDate(),
                commit.getDate(),
                commit.getDate(),
                commit.getDate(),
                commit.getDate(),
                commit.getDate());
        message("Date: %s", date);
        message("%s", commit.getMessage());
        message("");
        return;
    }

    public static void branchCommand(String branchName) {
        File branches = join(GITLET_DIR, "branches");
        File branchFile = join(branches, branchName);
        File currentBranch = join(GITLET_DIR, "currentBranch");
        File currentCommit = join(branches, readContentsAsString(currentBranch));
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        writeContents(branchFile, readContents(currentCommit));
    }

    public static void rmBranchCommand(String branchName) {
        File branches = join(GITLET_DIR, "branches");
        File branch = join(branches, branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        File currentBranch = join(GITLET_DIR, "currentBranch");
        if (readContentsAsString(currentBranch).equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branch.delete();
    }

    public static void globalLogCommand() {
        File commits = join(GITLET_DIR, "commits");
        List<String> list = plainFilenamesIn(commits);
        for (String fileName : list) {
            File commitFile = join(commits, fileName);
            Commit commit = readObject(commitFile, Commit.class);
            message("===");
            message("commit %s", commit.getCommitSHA1());
            if (commit.getSecondParentSHA1() != null) {
                message("Merge: " + commit.getFirstParentSHA1().substring(0, 7)
                        + " " + commit.getSecondParentSHA1().substring(0, 7));
            }
            String date = String.format(Locale.US,
                    "%ta %tb %te %tT %tY %tz",
                    commit.getDate(),
                    commit.getDate(),
                    commit.getDate(),
                    commit.getDate(),
                    commit.getDate(),
                    commit.getDate());
            message("Date: %s", date);
            message("%s", commit.getMessage());
            message("");
        }
    }

    public static void findCommand(String commitMessage) {
        File commits = join(GITLET_DIR, "commits");
        List<String> list = plainFilenamesIn(commits);
        boolean found = false;
        for (String fileName : list) {
            File commitFile = join(commits, fileName);
            Commit commit = readObject(commitFile, Commit.class);
            if (commit.getMessage().equals(commitMessage)) {
                System.out.println(commit.getCommitSHA1());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void rmCommand(String fileName) {
        File stagingFile = join(GITLET_DIR, "stagingFile");
        File removalFile = join(GITLET_DIR, "removalFile");
        Commit commit = getHeadCommit();
        File file = join(CWD, fileName);
        HashMap<String, String> stagingMap = readObject(stagingFile, HashMap.class);
        HashMap<String, String> commitMap = commit.getMap();
        HashSet<String> removalSet = new HashSet<>(readObject(removalFile, HashSet.class));
        if (!stagingMap.containsKey(fileName) && !commitMap.containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if (stagingMap.containsKey(fileName)) {
            stagingMap.remove(fileName);
            writeObject(stagingFile, stagingMap);
        }
        if (commitMap.containsKey(fileName)) {
            removalSet.add(fileName);
            writeObject(removalFile, removalSet);
            if (file.exists()) {
                restrictedDelete(file);
            }
        }
    }

    public static void statusCommand() {
        message("=== Branches ===");
        File branches = join(GITLET_DIR, "branches");
        List<String> branchNameList = plainFilenamesIn(branches);
        branchNameList.sort(null);
        File currentBranch = join(GITLET_DIR, "currentBranch");
        for (String branchName : branchNameList) {
            if (readContentsAsString(currentBranch).equals(branchName)) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }
        message("");

        File stagingFile = join(GITLET_DIR, "stagingFile");
        message("=== Staged Files ===");
        TreeMap<String, String> stagingMap =
                new TreeMap<>(readObject(stagingFile, HashMap.class));
        for (String fileName : stagingMap.keySet()) {
            System.out.println(fileName);
        }
        message("");

        message("=== Removed Files ===");
        File removalFile = join(GITLET_DIR, "removalFile");
        TreeSet<String> rmMap = new TreeSet<>(readObject(removalFile, HashSet.class));
        for (String fileName : rmMap) {
            System.out.println(fileName);
        }
        message("");

        message("=== Modifications Not Staged For Commit ===");
        TreeSet<String> output = new TreeSet<>();
        Commit commit = getHeadCommit();
        TreeMap<String, String> currentMap =
                new TreeMap<>(commit.getMap());
        File blobs = join(GITLET_DIR, "blobs");
        for (String fileName : currentMap.keySet()) {
            File file = join(CWD, fileName);
            File blob = join(blobs, currentMap.get(fileName));
            if (file.exists()
                    && !stagingMap.containsKey(fileName)
                    && !rmMap.contains(fileName)
                    && !Arrays.equals(readContents(file), readContents(blob))) {
                output.add(fileName + " (modified)");
            }
            if (!file.exists() && !rmMap.contains(fileName)) {
                output.add(fileName + " (deleted)");
            }
        }
        for (String fileName : stagingMap.keySet()) {
            File file = join(CWD, fileName);
            File blob = join(blobs, stagingMap.get(fileName));
            if (file.exists() && !Arrays.equals(readContents(file), readContents(blob))) {
                output.add(fileName + " (modified)");
            }
            if (!file.exists()) {
                output.add(fileName + " (deleted)");
            }
        }
        for (String fileName : output) {
            System.out.println(fileName);
        }
        message("");

        message("=== Untracked Files ===");
        List<String> fileList = plainFilenamesIn(CWD);
        for (String fileName : fileList) {
            File file = join(CWD, fileName);
            if ((!stagingMap.containsKey(fileName) && !currentMap.containsKey(fileName))
                    || (file.exists() && rmMap.contains(fileName))) {
                System.out.println(fileName);
            }
        }
        message("");
    }

    public static void resetCommand(String commitID) {
        File commits = join(GITLET_DIR, "commits");
        List<String> commitList = plainFilenamesIn(commits);
        int commitCount = 0;
        for (String commitName : commitList) {
            if (commitName.startsWith(commitID)) {
                commitID = commitName;
                commitCount++;
            }
        }
        if (!commitList.contains(commitID) || commitCount >= 2) {
            System.out.println("No commit with that id exists.");
            return;
        }
        File branches = join(GITLET_DIR, "branches");
        File branchFile = join(branches, getCurrentBranch());
        Commit currentCommit = getHeadCommit();
        File targetCommitFile = join(commits, commitID);
        Commit targetCommit = readObject(targetCommitFile, Commit.class);
        HashMap<String, String> targetMap = new HashMap<>(targetCommit.getMap());
        HashMap<String, String> currentMap = new HashMap<>(currentCommit.getMap());
        for (String fileName : targetMap.keySet()) {
            File file = join(CWD, fileName);
            if (file.exists() && !currentMap.containsKey(fileName)) {
                System.out.println(
                    "There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                return;
            }
        }
        File blobs = join(GITLET_DIR, "blobs");
        for (String fileName : targetMap.keySet()) {
            File blob = join(blobs, targetMap.get(fileName));
            File file = join(CWD, fileName);
            writeContents(file, readContents(blob));
        }
        for (String fileName : currentMap.keySet()) {
            if (!targetMap.containsKey(fileName)) {
                restrictedDelete(fileName);
            }
        }
        File stagingFile = join(GITLET_DIR, "stagingFile");
        File removalFile = join(GITLET_DIR, "removalFile");
        HashMap<String, String> stagingMap = new HashMap<>();
        HashSet<String> rmSet = new HashSet<>();
        writeObject(stagingFile, stagingMap);
        writeObject(removalFile, rmSet);
        writeContents(branchFile, targetCommit.getCommitSHA1());
    }

    public static void mergeCommand(String branchName) {
        File stagingFile = join(GITLET_DIR, "stagingFile");
        File removalFile = join(GITLET_DIR, "removalFile");
        if (!readObject(stagingFile, HashMap.class).isEmpty()
                || !readObject(removalFile, HashSet.class).isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        File branches = join(GITLET_DIR, "branches");
        File branch = join(branches, branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (branchName.equals(getCurrentBranch())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        Commit currentCommit = getHeadCommit();
        Commit givenCommit = getBranchCommit(branchName);
        Commit splitPointCommit = findSplitPoint(currentCommit, givenCommit);
        HashMap<String, String> currentMap = currentCommit.getMap();
        HashMap<String, String> givenMap = givenCommit.getMap();
        HashMap<String, String> splitMap = splitPointCommit.getMap();
        List<String> fileList = plainFilenamesIn(CWD);
        for (String file : fileList) {
            if (!currentMap.containsKey(file) && givenMap.containsKey(file)
                && !givenMap.get(file).equals(splitMap.get(file))) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                return;
            }
        }
        if (splitPointCommit.getCommitSHA1().equals(givenCommit.getCommitSHA1())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitPointCommit.getCommitSHA1().equals(currentCommit.getCommitSHA1())) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        boolean successfulMerge = mergeAllFile(branchName);
        mergeCommitCommand("Merged " + branchName + " into " + getCurrentBranch() + ".",
                getCurrentBranch(), branchName);
        if (!successfulMerge) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static boolean mergeAllFile(String branchName) {
        File blobs = join(GITLET_DIR, "blobs");
        File stagingFile = join(GITLET_DIR, "stagingFile");
        HashMap<String, String> stagingMap = readObject(stagingFile, HashMap.class);
        File removalFile = join(GITLET_DIR, "removalFile");
        HashSet<String> rmSet = readObject(removalFile, HashSet.class);
        Commit currentCommit = getHeadCommit();
        Commit givenCommit = getBranchCommit(branchName);
        HashMap<String, String> currentMap = currentCommit.getMap();
        HashMap<String, String> givenMap = givenCommit.getMap();
        Commit splitPointCommit = findSplitPoint(currentCommit, givenCommit);
        HashMap<String, String> splitMap = splitPointCommit.getMap();
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(splitMap.keySet());
        allFiles.addAll(currentMap.keySet());
        allFiles.addAll(givenMap.keySet());
        boolean successfulMerge = true;
        for (String fileName : allFiles) {
            File file = join(CWD, fileName);
            boolean inSplit = splitMap.containsKey(fileName);
            boolean inCurrent = currentMap.containsKey(fileName);
            boolean inGiven = givenMap.containsKey(fileName);
            String splitContent = splitMap.get(fileName);
            String currentContent = currentMap.get(fileName);
            String givenContent = givenMap.get(fileName);
            if (inGiven && inCurrent && inSplit
                    && !givenContent.equals(splitContent) && currentContent.equals(splitContent)) {
                File blob = join(blobs, givenContent);
                writeContents(file, readContents(blob));
                stagingMap.put(fileName, sha1(readContents(file)));
            } else if (inGiven && inCurrent && inSplit
                    && !currentContent.equals(splitContent) && givenContent.equals(splitContent)) {
                continue;
            } else if (inGiven && inCurrent && inSplit && currentContent.equals(givenContent)
                    && !currentContent.equals(splitContent) && !givenContent.equals(splitContent)) {
                continue;
            } else if (!inGiven && inCurrent && !inSplit) {
                continue;
            } else if (inGiven && !inCurrent && !inSplit) {
                File blob = join(blobs, givenContent);
                writeContents(file, readContents(blob));
                stagingMap.put(fileName, sha1(readContents(file)));
            } else if (inSplit && inCurrent && !inGiven && currentContent.equals(splitContent)) {
                restrictedDelete(fileName);
                rmSet.add(fileName);
            } else if (inSplit && inGiven && !inCurrent && givenContent.equals(splitContent)) {
                continue;
            } else if (!Objects.equals(givenContent, currentContent)) {
                successfulMerge = false;
                if (inGiven && inCurrent) {
                    File currBlob = join(blobs, currentContent);
                    File givenBlob = join(blobs, givenContent);
                    writeContents(file, "<<<<<<< HEAD\n",
                            readContentsAsString(currBlob), "=======\n",
                            readContentsAsString(givenBlob), ">>>>>>>\n");
                    File blob = join(blobs, sha1(readContents(file)));
                    writeContents(blob, readContents(file));
                    stagingMap.put(fileName, sha1(readContents(file)));
                } else if (!inCurrent && inGiven && inSplit && !givenContent.equals(splitContent)) {
                    File givenBlob = join(blobs, givenContent);
                    writeContents(file, "<<<<<<< HEAD\n", "=======\n",
                            readContentsAsString(givenBlob), ">>>>>>>\n");
                    File blob = join(blobs, sha1(readContents(file)));
                    writeContents(blob, readContents(file));
                    stagingMap.put(fileName, sha1(readContents(file)));
                } else if (inCurrent && !inGiven && inSplit
                    && !currentContent.equals(splitContent)) {
                    File currBlob = join(blobs, currentContent);
                    writeContents(file, "<<<<<<< HEAD\n",
                            readContentsAsString(currBlob), "=======\n", ">>>>>>>\n");
                    File blob = join(blobs, sha1(readContents(file)));
                    writeContents(blob, readContents(file));
                    stagingMap.put(fileName, sha1(readContents(file)));
                }
            }
        }
        writeObject(stagingFile, stagingMap);
        writeObject(removalFile, rmSet);
        return successfulMerge;
    }
}

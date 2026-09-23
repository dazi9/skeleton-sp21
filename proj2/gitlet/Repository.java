package gitlet;

import java.awt.*;
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
            Commit initCommit = new Commit("initial commit", null, m);
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
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        File stagingFile = join(GITLET_DIR, "stagingFile");
        HashMap<String, String> map =
                (HashMap<String, String>) readObject(stagingFile, HashMap.class);
        String blobSHA1 = sha1(readContents(file));
        File currentBranch = join(GITLET_DIR, "currentBranch");
        File branches = join(GITLET_DIR, "branches");
        File branchFile = join(branches, readContentsAsString(currentBranch));
        File commits = join(GITLET_DIR, "commits");
        File currentCommit = join(commits, readContentsAsString(branchFile));
        Commit commit = readObject(currentCommit, Commit.class);
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
            File currentBranch = join(GITLET_DIR, "currentBranch");
            File currentCommit = join(branches, readContentsAsString(currentBranch));
            File commits = join(GITLET_DIR, "commits");
            File parent = join(commits, readContentsAsString(currentCommit));
            HashMap<String, String> parentMap = readObject(parent, Commit.class).getMap();
            HashMap<String, String> map = new HashMap<>(parentMap);
            for (String key : stagingMap.keySet()) {
                map.put(key, stagingMap.get(key));
            }
            for (String key : rmMap) {
                map.remove(key);
            }
            rmMap.clear();
            writeObject(removalFile, rmMap);
            Commit commit = new Commit(message, readContentsAsString(currentCommit), map);
            File fileName = join(commits, commit.getCommitSHA1());
            writeObject(fileName, commit);
            writeContents(currentCommit, commit.getCommitSHA1());
            HashMap<String, String> m = new HashMap<>();
            writeObject(stagingFile, m);
        }
    }

    public static void checkoutFile(String fileName) {
        File branches = join(GITLET_DIR, "branches");
        File currentBranch = join(GITLET_DIR, "currentBranch");
        File commits = join(GITLET_DIR, "commits");
        File currentCommit = join(branches, readContentsAsString(currentBranch));
        File currentCommitFile = join(commits, readContentsAsString(currentCommit));
        Commit commit = readObject(currentCommitFile, Commit.class);
        checkoutCommitFromFile(commit, fileName);
    }

    public static void checkoutCommitFile(String commitID, String fileName) {
        File commits = join(GITLET_DIR, "commits");
        File commitFile = join(commits, commitID);
        if (!commitFile.exists()) {
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
        if (readContentsAsString(currentBranch).equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        File commits = join(GITLET_DIR, "commits");
        File currentBranchFile = join(branches, readContentsAsString(currentBranch));
        File currentCommit = join(commits, readContentsAsString(currentBranchFile));
        File commitFile = join(commits, readContentsAsString(branchFile));
        HashMap<String, String> targetMap =
                new HashMap<>(readObject(commitFile, Commit.class).getMap());
        HashMap<String, String> currentMap =
                new HashMap<>(readObject(currentCommit, Commit.class).getMap());
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
        File branches = join(GITLET_DIR, "branches");
        File currentBranch = join(GITLET_DIR, "currentBranch");
        File currentCommit = join(branches, readContentsAsString(currentBranch));
        File commits = join(GITLET_DIR, "commits");
        File commitFile = join(commits, readContentsAsString(currentCommit));
        Commit commit = readObject(commitFile, Commit.class);
        while (commit.getParentSHA1() != null) {
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
            File parentCommitFile = join(commits, commit.getParentSHA1());
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
        restrictedDelete(branch);
    }

    public static void globalLogCommand() {
        File commits = join(GITLET_DIR, "commits");
        List<String> list = plainFilenamesIn(commits);
        for (String fileName : list) {
            File commitFile = join(commits, fileName);
            Commit commit = readObject(commitFile, Commit.class);
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
        File currentBranch = join(GITLET_DIR, "currentBranch");
        File branches = join(GITLET_DIR, "branches");
        File currentCommitFile = join(branches, readContentsAsString(currentBranch));
        File commits = join(GITLET_DIR, "commits");
        File currentCommit = join(commits, readContentsAsString(currentCommitFile));
        Commit commit = readObject(currentCommit, Commit.class);
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
        File commits = join(GITLET_DIR, "commits");
        File branchFile = join(branches, readContentsAsString(currentBranch));
        File currentCommitFile = join(commits, readContentsAsString(branchFile));
        TreeMap<String, String> currentMap =
                new TreeMap<>(readObject(currentCommitFile, Commit.class).getMap());
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
        if (!commitList.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        File currentBranch = join(GITLET_DIR, "currentBranch");
        File branches = join(GITLET_DIR, "branches");
        File branchFile = join(branches, readContentsAsString(currentBranch));
        File commitFile = join(commits, readContentsAsString(branchFile));
        Commit currentCommit = readObject(commitFile, Commit.class);
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

    }
}

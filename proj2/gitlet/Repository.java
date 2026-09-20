package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */

    /* TODO: make an initial commit when initiating
     *
     */
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
            File initCommitFile = join(commits, initCommit.getCommitSHA1());
            writeObject(initCommitFile, initCommit);
            File current = join(GITLET_DIR, "current");
            writeContents(current, initCommit.getCommitSHA1());
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    @SuppressWarnings("unchecked")
    public static void addCommand(String fileName) {

        /* This section does the stagingFile part.
         *
         */
        File file = join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        File stagingFile = join(GITLET_DIR, "stagingFile");
        HashMap<String, String> map = (HashMap<String, String>) readObject(stagingFile, HashMap.class);
        String blobSHA1 = sha1(readContents(file));
        if (map.containsKey(fileName) && Objects.equals(map.get(fileName), blobSHA1)) {
            return;
        }
        map.put(fileName, blobSHA1);
        Utils.writeObject(stagingFile, map);

        /* This section does the blobs part.
         *
         */
        File blobs = join(GITLET_DIR, "blobs");
        File blob = join(blobs, blobSHA1);
        Utils.writeContents(blob, (Object) readContents(file));
    }

    public static void commitCommand(String message) {
        File stagingFile = join(GITLET_DIR, "stagingFile");
        if (readObject(stagingFile, HashMap.class).isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (message.isBlank()) {
            System.out.println("Please enter a commit message.");
            return;
        } else {
            File current = join(Repository.GITLET_DIR, "current");
            File commits = join(GITLET_DIR, "commits");
            File parent = join(commits, readContentsAsString(current));
            HashMap<String, String> parentMap = readObject(parent, Commit.class).getMap();
            HashMap<String, String> stagingMap = readObject(stagingFile, HashMap.class);
            HashMap<String, String> map = new HashMap<>(parentMap);
            for (String key : stagingMap.keySet()) {
                map.put(key, stagingMap.get(key));
            }
            Commit commit = new Commit(message, readContentsAsString(current), map);
            File fileName = join(commits, commit.getCommitSHA1());
            writeObject(fileName, commit);
            writeContents(current, commit.getCommitSHA1());
            HashMap<String, String> m = new HashMap<>();
            writeObject(stagingFile, m);
        }
    }

    public static void checkoutFile(String fileName) {
        File current = join(GITLET_DIR, "current");
        File commits = join(GITLET_DIR, "commits");
        File currentCommitFile = join(commits, readContentsAsString(current));
        Commit currentCommit = readObject(currentCommitFile, Commit.class);
        checkoutCommitFromFile(currentCommit, fileName);
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
            System.out.println("File does not exists in that commit.");
            return;
        }
        File blobs = join(GITLET_DIR, "blobs");
        File blob = join(blobs, commit.getMap().get(fileName));
        writeContents(file, readContents(blob));
    }

    public static void checkoutBranch(String branch) {

    }

    public static void logCommand() {
        File current = join(GITLET_DIR, "current");
        File commits = join(GITLET_DIR, "commits");
        File commitFile = join(commits, readContentsAsString(current));
        Commit commit = readObject(commitFile, Commit.class);
        while (commit.getParentSHA1() != null) {
            message("===");
            message("commit %s", commit.getCommitSHA1());
            String date = String.format(Locale.US,
                    "%ta %tb %td %tT %tY %tz",
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
                "%ta %tb %td %tT %tY %tz",
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
}

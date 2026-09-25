package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author dazi9
 */
public class Main {

    private static boolean requireArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            return false;
        }
        return true;
    }

    private static void cmdAdd(String[] args) {
        if (!requireArgs(args, 2)) {
            return;
        }
        Repository.checkInit();
        Repository.addCommand(args[1]);
    }

    private static void cmdCommit(String[] args) {
        if (!requireArgs(args, 2)) {
            return;
        }
        Repository.checkInit();
        Repository.commitCommand(args[1]);
    }

    private static void cmdLog(String[] args) {
        if (!requireArgs(args, 1)) {
            return;
        }
        Repository.checkInit();
        Repository.logCommand();
    }

    private static void cmdStatus(String[] args) {
        if (!requireArgs(args, 1)) {
            return;
        }
        Repository.checkInit();
        Repository.statusCommand();
    }

    private static void cmdGlobalLog(String[] args) {
        if (!requireArgs(args, 1)) {
            return;
        }
        Repository.checkInit();
        Repository.globalLogCommand();
    }

    private static void cmdBranch(String[] args) {
        if (!requireArgs(args, 2)) {
            return;
        }
        Repository.checkInit();
        Repository.branchCommand(args[1]);
    }

    private static void cmdRmBranch(String[] args) {
        if (!requireArgs(args, 2)) {
            return;
        }
        Repository.checkInit();
        Repository.rmBranchCommand(args[1]);
    }

    private static void cmdReset(String[] args) {
        if (!requireArgs(args, 2)) {
            return;
        }
        Repository.checkInit();
        Repository.resetCommand(args[1]);
    }

    private static void cmdRm(String[] args) {
        if (!requireArgs(args, 2)) {
            return;
        }
        Repository.checkInit();
        Repository.rmCommand(args[1]);
    }

    private static void cmdMerge(String[] args) {
        if (!requireArgs(args, 2)) {
            return;
        }
        Repository.checkInit();
        Repository.mergeCommand(args[1]);
    }

    private static void cmdFind(String[] args) {
        if (!requireArgs(args, 2)) {
            return;
        }
        Repository.checkInit();
        Repository.findCommand(args[1]);
    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        int len = args.length;
        switch (firstArg) {
            case "init":
                if (len == 1) {
                    Repository.initCommand();
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "add":
                cmdAdd(args);
                break;
            case "commit":
                cmdCommit(args);
                break;
            case "checkout":
                if (args.length == 3 && args[1].equals("--")) {
                    Repository.checkInit();
                    String fileName = args[2];
                    Repository.checkoutFile(fileName);
                } else if (args.length == 4 && args[2].equals("--")) {
                    Repository.checkInit();
                    String fileName = args[3];
                    String commitID = args[1];
                    Repository.checkoutCommitFile(commitID, fileName);
                } else if (args.length == 2) {
                    Repository.checkInit();
                    String branch = args[1];
                    Repository.checkoutBranch(branch);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "log":
                cmdLog(args);
                break;
            case "branch":
                cmdBranch(args);
                break;
            case "rm-branch":
                cmdRmBranch(args);
                break;
            case "global-log":
                cmdGlobalLog(args);
                break;
            case "find":
                cmdFind(args);
                break;
            case "rm":
                cmdRm(args);
                break;
            case "status":
                cmdStatus(args);
                break;
            case "reset":
                cmdReset(args);
                break;
            case "merge":
                cmdMerge(args);
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}

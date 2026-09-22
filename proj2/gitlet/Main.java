package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author dazi9
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                validateNumArgs("init", args, 1);
                Repository.initCommand();
                break;
            case "add":
                validateNumArgs("add", args, 2);
                String fileName = args[1];
                Repository.addCommand(fileName);
                break;
            case "commit":
                validateNumArgs("commit", args, 2);
                String message = args[1];
                Repository.commitCommand(message);
                break;
            case "checkout":
                if (args.length == 3 && args[1].equals("--")) {
                    fileName = args[2];
                    Repository.checkoutFile(fileName);
                } else if (args.length == 4 && args[2].equals("--")) {
                    fileName = args[3];
                    String commitID = args[1];
                    Repository.checkoutCommitFile(commitID, fileName);
                } else if (args.length == 2) {
                    String branch = args[1];
                    Repository.checkoutBranch(branch);
                }
                break;
            case "log":
                Repository.logCommand();
                break;
            case "branch":
                String branchName = args[1];
                Repository.branchCommand(branchName);
                break;
            case "rm-branch":
                branchName = args[1];
                Repository.rmBranchCommand(branchName);
                break;
            case "global-log":
                Repository.globalLogCommand();
                break;
            case "find":
                String commitMessage = args[1];
                Repository.findCommand(commitMessage);
                break;
            case "rm":
                fileName = args[1];
                Repository.rmCommand(fileName);
                break;
            case "status":
                Repository.statusCommand();
                break;
            case "reset":
                String commitID = args[1];
                Repository.resetCommand(commitID);
                break;
            case "merge":
                branchName = args[1];
                Repository.mergeCommand(branchName);
                break;
            default:
                break;
        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}

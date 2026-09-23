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
                if (len == 2) {
                    Repository.checkInit();
                    String fileName = args[1];
                    Repository.addCommand(fileName);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "commit":
                if (len == 2) {
                    Repository.checkInit();
                    String message = args[1];
                    Repository.commitCommand(message);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "checkout":
                Repository.checkInit();
                if (args.length == 3 && args[1].equals("--")) {
                    String fileName = args[2];
                    Repository.checkoutFile(fileName);
                } else if (args.length == 4 && args[2].equals("--")) {
                    String fileName = args[3];
                    String commitID = args[1];
                    Repository.checkoutCommitFile(commitID, fileName);
                } else if (args.length == 2) {
                    String branch = args[1];
                    Repository.checkoutBranch(branch);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "log":
                if (len == 1) {
                    Repository.checkInit();
                    Repository.logCommand();
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "branch":
                if (len == 2) {
                    Repository.checkInit();
                    String branchName = args[1];
                    Repository.branchCommand(branchName);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "rm-branch":
                if (len == 2) {
                    Repository.checkInit();
                    String branchName = args[1];
                    Repository.rmBranchCommand(branchName);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "global-log":
                if (len == 1) {
                    Repository.checkInit();
                    Repository.globalLogCommand();
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "find":
                if (len == 2) {
                    Repository.checkInit();
                    String commitMessage = args[1];
                    Repository.findCommand(commitMessage);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "rm":
                if (len == 2) {
                    Repository.checkInit();
                    String fileName = args[1];
                    Repository.rmCommand(fileName);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "status":
                if (len == 1) {
                    Repository.checkInit();
                    Repository.statusCommand();
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "reset":
                if (len == 2) {
                    Repository.checkInit();
                    String commitID = args[1];
                    Repository.resetCommand(commitID);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "merge":
                if (len == 2) {
                    Repository.checkInit();
                    String branchName = args[1];
                    Repository.mergeCommand(branchName);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}

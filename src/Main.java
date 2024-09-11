import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String url = "jdbc:mysql://localhost:3306/banking_system";
    private static final String userName = "root";
    private static final String password = "ravi";

    static Scanner sc = new Scanner(System.in);

    public static void CheckBalance() {
        try{
            Connection connection = DriverManager.getConnection(url, userName, password);
            System.out.println("Enter the Account Number: ");
            int account_no = sc.nextInt();

            String query = "SELECT balance FROM accounts WHERE account_no = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, account_no);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                double curr_balance = resultSet.getDouble("balance");
                System.out.println("Current Balance: " + curr_balance);
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void DebitAmount() {
        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            connection.setAutoCommit(false);

            String debitQuery = "UPDATE accounts set balance = balance - ? WHERE account_no = ?";

            PreparedStatement debitPreparedStatement = connection.prepareStatement(debitQuery);

            System.out.print("Enter the Account Number from which you want to DEBIT the amount: ");
            int account_no = sc.nextInt();

            System.out.print("Enter the Amount: ");
            double amount = sc.nextDouble();

            debitPreparedStatement.setDouble(1, amount);
            debitPreparedStatement.setInt(2, account_no);

            if (isSufficient(connection, account_no, amount)) {
                debitPreparedStatement.executeUpdate();
                connection.commit();
                System.out.println("Amount Debited Successfully!!");
            } else {
                connection.rollback();
                System.out.println("Insufficient Balance!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void CreditAmount() {
        try{
            Connection connection = DriverManager.getConnection(url, userName, password);
            String creditQuery = "UPDATE accounts set balance = balance + ? WHERE account_no = ?";

            PreparedStatement creditPreparedStatement = connection.prepareStatement(creditQuery);

            System.out.print("Enter the Account Number to which you want to CREDIT the amount: ");
            int account_no = sc.nextInt();

            System.out.print("Enter the Amount: ");
            double amount = sc.nextDouble();

            creditPreparedStatement.setDouble(1, amount);
            creditPreparedStatement.setInt(2, account_no);

            int rowsAffected = creditPreparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Amount Credited Successfully!");
            }

        } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
    }
    public static void TransferBalance() {
        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            connection.setAutoCommit(false);

            String debitQuery = "UPDATE accounts set balance = balance - ? WHERE account_no = ?";
            String creditQuery = "UPDATE accounts set balance = balance + ? WHERE account_no = ?";

            PreparedStatement debitPreparedStatement = connection.prepareStatement(debitQuery);
            PreparedStatement creditPreparedStatement = connection.prepareStatement(creditQuery);

            System.out.print("Enter the Account Number from which you want to DEBIT the amount: ");
            int account_no1 = sc.nextInt();

            System.out.print("Enter the Account Number to which you want to CREDIT the amount: ");
            int account_no2 = sc.nextInt();

            System.out.print("Enter the Amount: ");
            double amount = sc.nextDouble();

            debitPreparedStatement.setDouble(1, amount);
            debitPreparedStatement.setInt(2, account_no1);

            creditPreparedStatement.setDouble(1, amount);
            creditPreparedStatement.setInt(2, account_no2);

            if (isSufficient(connection, account_no1, amount)) {
                debitPreparedStatement.executeUpdate();
                creditPreparedStatement.executeUpdate();
                connection.commit();
                System.out.println("Transaction Successful! Amount Transferred Successfully!!");
            } else {
                connection.rollback();
                System.out.println("Transaction Failed! Insufficient Balance!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void AddAccount() {
        try{
            Connection connection = DriverManager.getConnection(url, userName, password);
            String insertQuery = "INSERT INTO accounts(account_no, name) VALUES (?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            System.out.print("Enter the Account Number: ");
            int account_no = sc.nextInt();
            System.out.print("Enter the first name: ");
            String firstName = sc.next();
            System.out.print("Enter the last name: ");
            String lastName = sc.next();

            String fullName = firstName + " " +lastName;

            preparedStatement.setInt(1, account_no);
            preparedStatement.setString(2, fullName);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account Added Successfully!");
            }

        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void DeleteAccount() {
       try{
           Connection connection = DriverManager.getConnection(url, userName, password);
           String deleteQuery = "DELETE FROM accounts WHERE account_no = ?";

           PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);

           System.out.print("Enter the Account Number: ");
           int account_no = sc.nextInt();

           preparedStatement.setInt(1, account_no);

           int rowsAffected = preparedStatement.executeUpdate();

           if (rowsAffected > 0) {
               System.out.println("Account Deleted Successfully!");
           }
       }catch (SQLException e) {
           System.out.println(e.getMessage());
       }
    }

    public static void ViewAccounts() {
        String query = "SELECT * FROM accounts";
        try{
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Accounts: ");
            System.out.println("+-------------+-------------+-------------+");
            System.out.println("| Account No. |    Name     |   Balance   |");
            System.out.println("+-------------+-------------+-------------+");
            while(resultSet.next()){
                int account_no = resultSet.getInt("account_no");
                String name = resultSet.getString("name");
                int balance = resultSet.getInt("balance");
                System.out.printf("| %-11s | %-11s | %-11s |\n", account_no, name, balance);
                System.out.println("+-------------+-------------+-------------+");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    static boolean isSufficient(Connection connection, int account_no, double amount) {
        boolean flag = true;
        try{
            String query = "SELECT balance FROM accounts WHERE account_no = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, account_no);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double curr_balance = resultSet.getDouble("balance");

                if (curr_balance < amount) {
                    return false;
                }
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return flag;
    }
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        int choice;
        do {
            System.out.println("1. Check Balance");
            System.out.println("2. Debit Balance");
            System.out.println("3. Credit Balance");
            System.out.println("4. Transfer Balance");
            System.out.println("5. Add Account");
            System.out.println("6. Delete Account");
            System.out.println("7. View All Accounts");
            System.out.println("8. To EXIT");

            System.out.print("Enter you Choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1: CheckBalance();
                        break;
                case 2: DebitAmount();
                        break;
                case 3: CreditAmount();
                        break;
                case 4: TransferBalance();
                        break;
                case 5: AddAccount();
                        break;
                case 6: DeleteAccount();
                        break;
                case 7: ViewAccounts();
                        break;
                case 8: break;
                default:
                    System.out.println("Please Enter Valid Choice!");
            }
        } while (choice != 8);
    }
}

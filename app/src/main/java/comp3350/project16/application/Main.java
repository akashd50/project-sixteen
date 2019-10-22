package comp3350.project16.application;

public class Main {

    private static String dbName="P16DB";

    public static void main(String[] args) {
        System.out.println("All done");
    }

    public static void setDBPathName(final String name) {

        System.out.println("Setting DB Path Name");
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dbName = name;
    }

    public static String getDBPathName() {
        return dbName;
    }

}

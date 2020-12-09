class Test {
    Person manager;

    public static void main(String[] args) {
        Person john = new Person();
        Person manager;

        manager = john.getDepartment().getManager();
    }
}

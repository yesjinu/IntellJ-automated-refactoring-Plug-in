class Department {
    private Person _manager;

    public Department(Person manager) {
        _manager = manager;
    }

    public Person getManager() {
        Person manager = new Person();
        return manager;
    }
}
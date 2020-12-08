class Person {
    Department _department;

    public Department getDepartment() {
        return _department;
    }

    public void setDepartment(Department arg) {
        _department = arg;
    }

    public Person getManager() {
        return _department.getManager();
    }
}

class Department {
    private Person _manager;

    public Department(Person manager) {
        _manager = manager;
    }

    public Person getManager() {
        return _manager;
    }
}

class Test {
    public static void main(String[] args) {
        Person john = new Person();
        Person manager;

        manager = john.getManager();
    }
}

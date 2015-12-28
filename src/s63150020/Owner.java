package s63150020;


enum Owner { 
    Me(Stroj_mn.NAME), Other("Other"), AssumePlayed(Stroj_mn.NAME + "-Assumed"), Empty("Empty");

    private String name; 

    private Owner(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

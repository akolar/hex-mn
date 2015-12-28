package s63150020;


enum Owner { 
    Me(Stroj_OrangePanda.NAME), Other("Other"), AssumePlayed(Stroj_OrangePanda.NAME + "-Assumed"), Empty("Empty");

    private String name; 

    private Owner(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

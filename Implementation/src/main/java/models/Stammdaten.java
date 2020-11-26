package models;

import models.Person.Person;

import java.util.List;

public class Stammdaten {
    private static Stammdaten instance;
    private List<Person> personen;

    private Stammdaten(){}

    public static Stammdaten getInstance(){
        if(Stammdaten.instance == null){
            Stammdaten.instance = new Stammdaten();
        }
        return Stammdaten.instance;
    }

    public List<Person> getPersonen() {
        if(personen == null || personen.isEmpty()){
            personen = initPersonen();
        }
        return personen;
    }

    public List<Person> initPersonen(){
        //DB function here;
        return null;
    }
}

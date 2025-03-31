package src.main.java.internet;


public class Deconnexion extends Exception{
    private String message;
    public Deconnexion(String message){
        this.message=message;
    }
    public Deconnexion(){

    }
    public String getString(){
        if(message!=null){
            return message;
        }else{
            return "";
        }
    }
}

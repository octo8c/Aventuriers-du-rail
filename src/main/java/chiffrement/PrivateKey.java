package src.main.java.chiffrement;

import java.math.BigInteger;

public record PrivateKey(BigInteger p,BigInteger a) {
    @Override
    public final String toString() {
        return "[ Cle : "+p.toString()+" ,a :"+a.toString()+"]";

    }
}

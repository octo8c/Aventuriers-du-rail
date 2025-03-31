package src.main.java.chiffrement;

import java.io.Serializable;
import java.math.BigInteger;
/*
 * La forme des message cryptes
 */

public record CryptedMessage(BigInteger bigB,BigInteger c,BigInteger r,BigInteger s) implements Serializable{
    @Override
    public final String toString() {
        return "[ c1 : "+bigB+" , c2 : "+c+", r : "+r+", s : "+s+"]";
    }
}

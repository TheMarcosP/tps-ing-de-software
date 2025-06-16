package org.udesa.unoback.model;

import java.util.Objects;

public abstract class ColoredCard extends Card {
    protected String color = "";
    public static String Red = "Red";
    public static String Blue = "Blue";
    public static String Green = "Green";
    public static String Yellow = "Yellow";

    public ColoredCard( String aColor ) {
        color = aColor;
    }
    public boolean acceptsOnTop( Card aCard ) { return  aCard.yourColorIs( color() );   }
    public boolean yourColorIs( String aColor ) { return color.equals( aColor );  }
    public String color() { return color;  }

    public boolean equals( Object o ) { return super.equals( o ) && color.equals( ColoredCard.class.cast( o ).color );  }
    public int hashCode() {             return Objects.hash( color );}

    public JsonCard asJson() { return new JsonCard( color, null, getClass().getSimpleName(), unoShouted() ); }
}

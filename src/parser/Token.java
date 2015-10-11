package parser;

import java.io.Serializable;

/**
 * Describes the input token stream.
 */
public class Token implements Serializable
{
    /**
     * The version identifier for this Serializable class. Increment only if the
     * <i>serialized</i> form of the class changes.
     */
    private static final long serialVersionUID = 1L;

    /**
     * An integer that describes the kind of this token.
     */
    public int kind = -1;

    /** The line number of the first character of this Token. */
    public int beginLine = 0;
    /** The column number of the first character of this Token. */
    public int beginColumn = 0;
    /** The line number of the last character of this Token. */
    public int endLine = 0;
    /** The column number of the last character of this Token. */
    public int endColumn = 0;

    /**
     * The string image of the token.
     */
    public String image = "";

    /**
     * A reference to the next regular (non-special) token from the input
     * stream. If this is the last token from the input stream, or if the token
     * manager has not read tokens beyond this one, this field is set to null.
     * This is true only if this token is also a regular token. Otherwise, see
     * below for a description of the contents of this field.
     */
    public Token next = null;
    public Token prev = null;

    /** 
     * this token is special one(such as in defined macro)
     */
    public boolean special = false;

    /**
     * No-argument constructor
     */
    public Token() {
    }

    /**
     * Constructs a new token for the specified Image.
     */
    public Token(int kind) {
        this(kind, "");
    }

    /**
     * Constructs a new token for the specified Image and Kind.
     */
    public Token(int kind, String image) {
        this.kind = kind;
        this.image = image;
    }

    /**
     * Returns the image.
     */
    public String toString() {
        return image;
    }
    
    public String dump() {
        String ret = "";
        ret = String.format("Error Token:%s, Line:%d, Column:%d\n", image, 
                beginLine, beginColumn);
        return ret;
    }
}


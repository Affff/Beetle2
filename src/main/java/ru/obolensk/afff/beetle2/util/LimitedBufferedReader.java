package ru.obolensk.afff.beetle2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.annotation.Nonnull;

import lombok.Getter;

/**
 * Created by Afff on 20.04.2017.
 */
public class LimitedBufferedReader extends BufferedReader {

    private static final int CTRL_CH_COUNT = 2; // CR + LF

    private final int maxLineLength;

    private boolean lineOverflow;

    @Getter
    private int lastLineSize;

    public LimitedBufferedReader(@Nonnull final Reader reader, final int maxLineLength) {
        super(reader);
        if (maxLineLength <= 0) {
            throw new IllegalArgumentException("maxLineLength must be positive!");
        }
        this.maxLineLength = maxLineLength;
    }

    @Override
    public String readLine() throws IOException {
        final char[] data = new char[maxLineLength];
        final int CR = 13;
        final int LF = 10;

        int currentPos = 0;
        int currentCharVal = super.read();

        while( (currentCharVal != CR) && (currentCharVal != LF) && (currentCharVal >= 0)) {
            data[currentPos++] = (char) currentCharVal;
            if (currentPos < maxLineLength) {
                currentCharVal = super.read();
            } else {
                lineOverflow = true;
                return new String(data, 0, currentPos);
            }
        }

        if (currentCharVal < 0 ) {
            // stream is over, return current buffer or null if the buffer is empty
            if (currentPos > 0) {
                lastLineSize = currentPos;
                return new String(data, 0, currentPos);
            } else {
                return null;
            }
        } else {
            if (currentCharVal == CR) {
                //Check for LF and remove from buffer
                super.mark(1);
                if (super.read() != LF) {
                    super.reset();
                }
            }
            lastLineSize = currentPos + CTRL_CH_COUNT;
            return new String(data, 0, currentPos);
        }
    }

    /**
     * Returns true if last line was too long and false otherwise.
     * WARNING! Overflow status is raised by call this method.
     */
    public boolean isLineOverflow() {
        boolean overflow = lineOverflow;
        if (overflow) {
            lineOverflow = false;
        }
        return overflow;
    }
}

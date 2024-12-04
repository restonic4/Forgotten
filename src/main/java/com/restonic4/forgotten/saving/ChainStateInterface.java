package com.restonic4.forgotten.saving;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface ChainStateInterface extends Component {
    boolean isAlt();
    void setAlt(boolean value);
    boolean isVertical();
    void setVertical(boolean value);
    boolean isRotated();
    void setRotated(boolean value);
    int getIndex();
    void setIndex(int value);
    boolean isDed();
    void setDed(boolean value);
}

package com.restonic4.forgotten.saving;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface ChainStateInterface extends Component {
    boolean isAlt();
    void setAlt(boolean value);
    boolean isVertical();
    void setVertical(boolean value);
}
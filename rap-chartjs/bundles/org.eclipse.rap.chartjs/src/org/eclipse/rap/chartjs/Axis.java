package org.eclipse.rap.chartjs;

public class Axis
{
    boolean display = true;
    boolean stacked = false;
    
    private Ticks ticks = new Ticks();

    public Ticks getTicks()
    {
        return ticks;
    }
    
    public void setDisplay(boolean display)
    {
        this.display = display;
    }
    
    public boolean isDisplay()
    {
        return display;
    }
    
    public void setStacked(boolean stacked)
    {
        this.stacked = stacked;
    }
    public boolean isStacked()
    {
        return stacked;
    }
}

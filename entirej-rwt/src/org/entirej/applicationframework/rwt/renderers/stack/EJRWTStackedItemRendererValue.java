package org.entirej.applicationframework.rwt.renderers.stack;


public class EJRWTStackedItemRendererValue
{

    private EJRWTStackedItemRendererConfig config = new EJRWTStackedItemRendererConfig.Spacer();
    private Object                      value;

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public EJRWTStackedItemRendererConfig getConfig()
    {
        return config;
    }

    public void setConfig(EJRWTStackedItemRendererConfig config)
    {
        this.config = config;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EJRWTStackedItemRendererValue other = (EJRWTStackedItemRendererValue) obj;

        if (value == null)
        {
            if (other.value != null)
                return false;
        }
        else if (!value.equals(other.value))
            return false;
        return true;
    }

}

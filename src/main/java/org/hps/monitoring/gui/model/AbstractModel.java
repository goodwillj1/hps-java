package org.hps.monitoring.gui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An abstract class which updates a set of listeners when there are property changes to a backing model.
 * @author Jeremy McCormick <jeremym@slac.stanford.edu>
 */
public abstract class AbstractModel {

    protected PropertyChangeSupport propertyChangeSupport;
    protected boolean listenersEnabled = true;    

    public AbstractModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void setListenersEnabled(boolean listenersEnabled) {
        this.listenersEnabled = listenersEnabled;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (listenersEnabled)
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        // System.out.println("firePropertyChange");
        // System.out.println("  name: " + propertyName);
        // System.out.println("  old value: " + oldValue);
        // System.out.println("  new value: " + newValue);
    }

    protected void firePropertyChange(PropertyChangeEvent evt) {
        if (listenersEnabled)
            propertyChangeSupport.firePropertyChange(evt);
    }

    abstract public String[] getPropertyNames();

    // FIXME: This method is kind of a hack. Any other good way to do this?
    public void fireAllChanged() {
        if (!listenersEnabled)
            return;
       propertyLoop: for (String property : getPropertyNames()) {            
            Method getMethod = null;
            for (Method method : getClass().getMethods()) {
                if (method.getName().equals("get" + property)) {
                    getMethod = method;
                    break;
                }
            }
            try {
                Object value = null;
                try {
                    value = getMethod.invoke(this, (Object[]) null);
                } catch (NullPointerException e) {
                    // This means there is no get method for the property which is a throwable error.
                    throw new RuntimeException("Property " + property + " is missing a get method.", e);
                } catch (InvocationTargetException e) {
                    // Is the cause of the problem an illegal argument to the method?
                    System.out.println("cause: " + e.getCause().getClass().getCanonicalName());
                    if (e.getCause() instanceof IllegalArgumentException) {
                        // For this error, assume that the key itself is missing from the configuration which is a warning.
                        System.err.println("The key " + property + " is not set in the configuration.");
                        continue propertyLoop;
                    } else {
                        throw new RuntimeException(e);
                    }
                }
                // Is the value non-null?  Null values are actually okay.
                //if (value != null) {
                firePropertyChange(property, value, value);
                for (PropertyChangeListener listener : propertyChangeSupport.getPropertyChangeListeners()) {
                    // FIXME: For some reason calling the propertyChangeSupport methods directly here doesn't work!
                    listener.propertyChange(new PropertyChangeEvent(this, property, value, value));
                }
                //}
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
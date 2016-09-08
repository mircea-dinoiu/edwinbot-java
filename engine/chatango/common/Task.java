package engine.chatango.common;

import engine.chatango.manager.StreamManager.StreamManager;

import java.lang.reflect.Method;

public class Task {
    private StreamManager manager;
    private Object invokeFrom;
    private long target;
    private long timeout;
    private Method method;
    private boolean isInterval = false;
    private Object[] args;

    /**
     * Cancel the task.
     */
    public void cancel() {
        manager.removeTask(this);
    }

    /**
     * Manager
     */
    public void setManager(StreamManager manager) {
        this.manager = manager;
    }

    /**
     * Invoke from
     */
    public Object getInvokeFrom() {
        return invokeFrom;
    }

    public void setInvokeFrom(Object invokeFrom) {
        this.invokeFrom = invokeFrom;
    }

    /**
     * Timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
        resetTimeout();
    }

    public void resetTimeout() {
        this.target = System.currentTimeMillis() + timeout * 1000;
    }

    /**
     * Target
     */
    public long getTarget() {
        return target;
    }

    /**
     * Method to call
     */
    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Interval flag
     */
    public boolean isInterval() {
        return isInterval;
    }

    public void setIsInterval(boolean isInterval) {
        this.isInterval = isInterval;
    }

    /**
     * Args
     */
    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}

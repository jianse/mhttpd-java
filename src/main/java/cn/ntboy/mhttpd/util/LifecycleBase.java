package cn.ntboy.mhttpd.util;

import cn.ntboy.mhttpd.Lifecycle;
import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LifecycleBase implements Lifecycle{

    Logger logger = LogManager.getLogger(LifecycleBase.class);

    private volatile LifecycleState state = LifecycleState.NEW;

    @Override
    public final synchronized void init() throws LifecycleException {

        if (!state.equals(LifecycleState.NEW)) {
            invalidTransition(Lifecycle.BEFORE_INIT_EVENT);
        }

        try {
            setStateInternal(LifecycleState.INITIALIZING, false);
            initInternal();
            setStateInternal(LifecycleState.INITIALIZED, false);
        } catch (Throwable t) {
            throw new LifecycleException(t);
        }

    }

    protected abstract void initInternal() throws LifecycleException;

    @Override
    public final synchronized void start() throws LifecycleException {
        if (LifecycleState.STARTING_PREP.equals(state) || LifecycleState.STARTING.equals(state) ||
                LifecycleState.STARTED.equals(state)) {
            //already call start
            return;
        }

        if (state.equals(LifecycleState.NEW)) {
            init();
        } else if (state.equals(LifecycleState.FAILED)) {
            stop();
        } else if (!state.equals(LifecycleState.INITIALIZED) && !state.equals(LifecycleState.STOPPED)) {
            invalidTransition(Lifecycle.BEFORE_START_EVENT);
        }

        try {
            setStateInternal(LifecycleState.STARTING_PREP, false);
            startInternal();
            if (state.equals(LifecycleState.FAILED)) {
                stop();
            } else if (!state.equals(LifecycleState.STARTING)) {
                invalidTransition(Lifecycle.AFTER_START_EVENT);
            } else {
                setStateInternal(LifecycleState.STARTED, false);
            }
        } catch (Throwable t) {

            logger.error(t);
            t.printStackTrace();
            throw new LifecycleException(t);
        }

    }

    protected abstract void startInternal() throws LifecycleException;

    @Override
    public final synchronized void stop() throws LifecycleException {
        if (LifecycleState.STOPPING_PREP.equals(state) || LifecycleState.STOPPING.equals(state) || LifecycleState.STOPPED.equals(state)) {
            return;
        }

        if (state.equals(LifecycleState.NEW)) {
            state = LifecycleState.STOPPED;
            return;
        }

        if (!state.equals(LifecycleState.STARTED) && !state.equals(LifecycleState.FAILED)) {
            invalidTransition(Lifecycle.BEFORE_STOP_EVENT);
        }

        try {
            if (state.equals(LifecycleState.FAILED)) {
                //todo:fire the event to handle this
            } else {
                setStateInternal(LifecycleState.STOPPING_PREP, false);
            }

            stopInternal();

            if (!state.equals(LifecycleState.STOPPING) && !state.equals(LifecycleState.FAILED)) {
                invalidTransition(Lifecycle.AFTER_STOP_EVENT);
            }
            setStateInternal(LifecycleState.STOPPED, false);

        } catch (Throwable t) {
            throw new LifecycleException(t);
        } finally {
            if (this instanceof Lifecycle.SingleUse) {
                setStateInternal(LifecycleState.STOPPED, false);
                destroy();
            }
        }
    }

    protected abstract void stopInternal() throws LifecycleException;

    @Override
    public final synchronized void destroy() throws LifecycleException {
        if (LifecycleState.FAILED.equals(state)) {
            try {
                stop();
            } catch (LifecycleException e) {
                //todo:some log
            }
        }

        if (LifecycleState.DESTROYING.equals(state) || LifecycleState.DESTROYED.equals(state)) {
            return;
        }

        if (!state.equals(LifecycleState.STOPPED) && !state.equals(LifecycleState.FAILED) &&
                !state.equals(LifecycleState.NEW) && !state.equals(LifecycleState.INITIALIZED)) {
            invalidTransition(Lifecycle.BEFORE_DESTROY_EVENT);
        }

        try {
            setStateInternal(LifecycleState.DESTROYING, false);
            destroyInternal();
            setStateInternal(LifecycleState.DESTROYED, false);
        } catch (Throwable t) {
            throw new LifecycleException(t);
        }

    }

    protected abstract void destroyInternal() throws LifecycleException;

    protected synchronized void setStateInternal(LifecycleState state, boolean check) throws LifecycleException {
        if (check) {
            if (state == null) {
                invalidTransition("null");
                return;
            }
            // any to failed
            // starting_prep to starting
            // stopping_prep to stopping
            // failed to stopping
            if (!(state == LifecycleState.FAILED ||
                    (this.state == LifecycleState.STARTING_PREP && state == LifecycleState.STARTING) ||
                    (this.state == LifecycleState.STOPPING_PREP && state == LifecycleState.STOPPING) ||
                    (this.state == LifecycleState.FAILED && state == LifecycleState.STOPPING))) {
                //No other
                invalidTransition(state.name());

            }
        }
        this.state = state;
    }

    private void invalidTransition(String type) throws LifecycleException {
        throw new LifecycleException(type);
    }

    @Override
    public LifecycleState getState() {
        return state;
    }

    public void setState(LifecycleState state) throws LifecycleException {
        setStateInternal(state, true);
    }
}

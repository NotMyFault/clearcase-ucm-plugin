package net.praqma.hudson;

import java.io.File;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import net.praqma.clearcase.ucm.entities.*;
import hudson.model.Action;
import java.util.ArrayList;
import net.praqma.clearcase.ucm.view.SnapshotView;
import net.praqma.hudson.scm.Polling;
import net.praqma.hudson.scm.Unstable;
import net.praqma.hudson.scm.pollingmode.PollingMode;

public class CCUCMBuildAction implements Action {

    private AbstractBuild<?, ?> build;
    private transient TaskListener listener;
    private PollingMode mode;
	private Stream stream;
	private Component component;
    private Project.PromotionLevel promotionLevel;
    private String loadModule;
    private List<Baseline> rebaseTargets = new ArrayList<Baseline>();
    private List<Baseline> newFoundationStructure = new ArrayList<Baseline>();

    /**
     * Create a {@link Baseline} when deliver
     */
    private boolean createBaseline = true;

    /**

     * The naming template for the created {@link Baseline}
     */
    private String nameTemplate;

    private boolean setDescription = false;
    private boolean makeTag = false;
    private boolean recommend = false;
    private boolean forceDeliver = false;

    /**
     * Determines whether to swipe the view or not.
     *
     * @since 1.4.0
     */
    private boolean removeViewPrivateFiles = true;

    /**
     * Determine whether to trim the change set or not.
     * Not persisted due to large memory foot print.
     *
     * @since 1.4.0
     */
    private transient boolean trimmedChangeSet = false;

    /**
     * The found {@link Baseline} for the build
     */
	private Baseline baseline;

    private boolean addedByPoller = false;

    /**
     * The list of found {@link Baseline}'s. Typically used when polled
     */
    //private List<Baseline> baselines = null;

    /**
     * The type of polling
     */
    private Polling polling;

    /**
     * Determine whether to treat an unstable build as failed or successful
     */
    private Unstable unstable;

    /**
     * Determines whether the post build step should complete the deliver
     */
    private boolean needsToBeCompleted = true;

	/* View, possibly remote */
	private File viewPath;
	private String viewTag;
    private String workspace;

    /** The {@link SnapshotView} used during the build.<br/>
     * This not persisted. */
    private transient SnapshotView snapshotView;

    /*  */
    private Exception resolveBaselineException;

    /**
     * The created {@link Baseline} when deliver
     */
	private Baseline createdBaseline;

    private String error;

    private List<Activity> activities;

	public CCUCMBuildAction( Stream stream, Component component ) {
		this.stream = stream;
		this.component = component;
	}

    public void setBuild( AbstractBuild<?, ?> build ) {
        this.build = build;
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public TaskListener getListener() {
        return listener;
    }

    public void setListener( TaskListener listener ) {
        this.listener = listener;
    }

    public Baseline getBaseline() {
		return baseline;
	}

	public void setBaseline( Baseline baseline ) {
		this.baseline = baseline;
	}

    public void setStream( Stream stream ) {
        this.stream = stream;
    }

    public void setComponent( Component component ) {
        this.component = component;
    }

	public Baseline getCreatedBaseline() {
		return createdBaseline;
	}

	public void setCreatedBaseline( Baseline createdBaseline ) {
		this.createdBaseline = createdBaseline;
	}
	
	public void setViewPath( File path ) {
		this.viewPath = path;
	}
	
	public File getViewPath() {
		return viewPath;
	}

	public String getViewTag() {
		return viewTag;
	}

	public void setViewTag( String viewTag ) {
		this.viewTag = viewTag;
	}

    public SnapshotView getSnapshotView() {
        return snapshotView;
    }

    public void setSnapshotView( SnapshotView snapshotView ) {
        this.snapshotView = snapshotView;
    }

    public Stream getStream() {
		return stream;
	}

	public Component getComponent() {
		return component;
	}

    public Project.PromotionLevel getPromotionLevel() {
        return promotionLevel;
    }

    public void setPromotionLevel( Project.PromotionLevel promotionLevel ) {
        this.promotionLevel = promotionLevel;
    }

    public String getLoadModule() {
        return loadModule;
    }

    public void setLoadModule( String loadModule ) {
        this.loadModule = loadModule;
    }

    public boolean doCreateBaseline() {
        return createBaseline;
    }

    public void setCreateBaseline( boolean createBaseline ) {
        this.createBaseline = createBaseline;
    }

    public String getNameTemplate() {
        return nameTemplate;
    }

    public void setNameTemplate( String nameTemplate ) {
        this.nameTemplate = nameTemplate;
    }

    public boolean doSetDescription() {
        return setDescription;
    }

    public void setDescription( boolean setDescription ) {
        this.setDescription = setDescription;
    }

    public boolean doMakeTag() {
        return makeTag;
    }

    public void setMakeTag( boolean makeTag ) {
        this.makeTag = makeTag;
    }

    public boolean doRecommend() {
        return recommend;
    }

    public void setRecommend( boolean recommend ) {
        this.recommend = recommend;
    }

    public boolean doForceDeliver() {
        return forceDeliver;
    }

    public void setForceDeliver( boolean forceDeliver ) {
        this.forceDeliver = forceDeliver;
    }

    public boolean isAddedByPoller() {
        return addedByPoller;
    }

    public void setAddedByPoller( boolean addedByPoller ) {
        this.addedByPoller = addedByPoller;
    }

    /*
    public List<Baseline> getBaselines() {
        return baselines;
    }

    public void setBaselines( List<Baseline> baselines ) {
        this.baselines = baselines;
    }
    */

    public Polling getPolling() {
        return polling;
    }

    public void setPolling( Polling polling ) {
        this.polling = polling;
    }

    public Unstable getUnstable() {
        return unstable;
    }

    public void setUnstable( Unstable unstable ) {
        this.unstable = unstable;
    }

    public boolean doNeedsToBeCompleted() {
        return needsToBeCompleted;
    }

    public void setNeedsToBeCompleted( boolean needsToBeCompleted ) {
        this.needsToBeCompleted = needsToBeCompleted;
    }

    public String getError() {
        return error;
    }

    public void setError( String error ) {
        this.error = error;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace( String workspace ) {
        this.workspace = workspace;
    }

    @Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public String getIconFileName() {
		return null;
	}

	@Override
	public String getUrlName() {
		return null;
	}

    public boolean doRemoveViewPrivateFiles() {
        return removeViewPrivateFiles;
    }

    public void setRemoveViewPrivateFiles( boolean removeViewPrivateFiles ) {
        this.removeViewPrivateFiles = removeViewPrivateFiles;
    }

    public boolean isTrimmedChangeSet() {
        return trimmedChangeSet;
    }

    public void setTrimmedChangeSet( boolean trimmedChangeSet ) {
        this.trimmedChangeSet = trimmedChangeSet;
    }

    public Exception getResolveBaselineException() {
        return resolveBaselineException;
    }

    public void setResolveBaselineException( Exception resolveBaselineException ) {
        this.resolveBaselineException = resolveBaselineException;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities( List<Activity> activities ) {
        this.activities = activities;
    }

    @Override
    public String toString() {
        return stream + ", " + component + ", " + promotionLevel + " = " + baseline;
    }

    public String stringify() {
        StringBuilder sb = new StringBuilder().append("\n");        
        sb.append( "Stream          : ").append(stream).append("\n" );
        sb.append( "Component       : ").append(component).append("\n" );
        sb.append( "Promotion Level : ").append(promotionLevel).append("\n" );
        sb.append( "Baseline        : ").append(baseline).append("\n" );
        sb.append( "Created Baseline: ").append(createdBaseline).append("\n" );
        sb.append( "Polling         : ").append(polling).append("\n" );
        return sb.toString();
    }

    /**
     * @return the rebaseTargets
     */
    public List<Baseline> getRebaseTargets() {
        return rebaseTargets;
    }

    /**
     * @param rebaseTargets the rebaseTargets to set
     */
    public void setRebaseTargets(List<Baseline> rebaseTargets) {
        this.rebaseTargets = rebaseTargets;
    }
    
    public List<Baseline> getNewFoundationStructure() {
        return this.newFoundationStructure;
    }

    public void setNewFoundationStructure(List<Baseline> newFoundationStructure) {
        this.newFoundationStructure = newFoundationStructure;
    }

    /**
     * @return the mode
     */
    public PollingMode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(PollingMode mode) {
        this.mode = mode;
    }
    
}

package ui;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import query.Query;
import twitter.LiveTwitterSource;
import twitter.TwitterSource;
import util.SphericalGeometry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * The Twitter viewer application
 * Derived from a JMapViewer demo program written by Jan Peter Stotz
 */
public class Application extends JFrame {
    // The content panel, which contains the entire UI
    private final ContentPanel contentPanel;
    // The provider of the tiles for the map, we use the Bing source
    private BingAerialTileSource bing;

    //Initialization will be done from the new class QueryConfiguration
    private QueryConfiguration queryConfiguration;
    public QueryConfiguration getQueryConfiguration() {
        return queryConfiguration;
    }

    /**
     * Constructs the {@code Application}.
     */
    public Application() {
        super("Twitter content viewer");
        setSize(300, 300);
        contentPanel = new ContentPanel(this);
        queryConfiguration = new QueryConfiguration(contentPanel);
        bing = new BingAerialTileSource();

        //UI initialization
        initializeUI();

        //Map Configuration
        mapConfig();

        //Loading Bing
        bingLoad();

        //Tooltip Configuration
        tooltipConfig();
    }

    private void tooltipConfig() {
        // Set up a motion listener to create a tooltip showing the tweets at the pointer position
        map().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                Point point = event.getPoint();
                ICoordinate position = map().getPosition(point);
                // TODO: Use the following method to set the text that appears at the mouse cursor
                List<MapMarker> markers = getMarkersCovering(position, pixelWidth(point));
                MapMarker marker = markers.get(markers.size() - 1);
                MapMarkerImage mapMarkerImage = (MapMarkerImage) marker;
                String tweet = mapMarkerImage.getTweet();
                String profilePictureURL = mapMarkerImage.getImageUrl();
                map().setToolTipText("<html><img src=" + profilePictureURL + " height=\"39\" width=\"39\">" + tweet + "</html>");
            }
        });
    }

    private void bingLoad() {
        //NOTE This is so that the map eventually loads the tiles once Bing attribution is ready.
        Coordinate coordinate = new Coordinate(0, 0);

        Timer bingTimer = new Timer();
        TimerTask bingAttributionCheck = new TimerTask() {
            @Override
            public void run() {
                // This is the best method we've found to determine when the Bing data has been loaded.
                // We use this to trigger zooming the map so that the entire world is visible.
                if (!bing.getAttributionText(0, coordinate, coordinate).equals("Error loading Bing attribution data")) {
                    map().setZoom(2);
                    bingTimer.cancel();
                }
            }
        };
        bingTimer.schedule(bingAttributionCheck, 100, 200);
    }

    private void mapConfig() {
        // Always have map markers showing.
        map().setMapMarkerVisible(true);
        // Always have zoom controls showing,
        // and allow scrolling of the map around the edge of the world.
        map().setZoomContolsVisible(true);
        map().setScrollWrapEnabled(true);
        // Use the Bing tile provider
        map().setTileSource(bing);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    // How big is a single pixel on the map?  We use this to compute which tweet markers
    // are at the current most position.
    private double pixelWidth(Point p) {
        ICoordinate center = map().getPosition(p);
        ICoordinate edge = map().getPosition(new Point(p.x + 1, p.y));
        return SphericalGeometry.distanceBetween(center, edge);
    }

    // Get those layers (of tweet markers) that are visible because their corresponding query is enabled
    private Set<Layer> getVisibleLayers() {
        Set<Layer> visibleLayers = new HashSet<>();
        for (Query query : queryConfiguration) {
            if (query.getVisible()) {
                visibleLayers.add(query.getLayer());
            }
        }
        return visibleLayers;
    }

    // Get all the markers at the given map position, at the current map zoom setting
    private List<MapMarker> getMarkersCovering(ICoordinate pos, double pixelWidth) {
        List<MapMarker> markersCovering = new ArrayList<>();
        Set<Layer> visibleLayers = getVisibleLayers();
        for (MapMarker m : map().getMapMarkerList()) {
            if (!visibleLayers.contains(m.getLayer())) continue;
            double distance = SphericalGeometry.distanceBetween(m.getCoordinate(), pos);
            if (distance < m.getRadius() * pixelWidth) {
                markersCovering.add(m);
            }
        }
        return markersCovering;
    }

    public JMapViewer map() {
        return contentPanel.getViewer();
    }

    /**
     * @param args Application program arguments (which are ignored)
     */
    public static void main(String[] args) {
        new Application().setVisible(true);
    }

    // Update which queries are visible after any checkBox has been changed
    public void updateVisibility() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.out.println("Recomputing visible queries");
                for (Query query : queryConfiguration) {
                    JCheckBox box = query.getCheckBox();
                    Boolean state = box.isSelected();
                    query.setVisible(state);
                }
                map().repaint();
            }
        });
    }
}

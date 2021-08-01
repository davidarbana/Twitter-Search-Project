package ui;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MapMarkerImage extends MapMarkerCircle {
    public static final double defaultMarkerSize = 15.0;
    public BufferedImage image;
    public String tweet;
    public String imageUrl;

    public MapMarkerImage(Layer layer, Coordinate coordinate, Color color, String imageUrl, String tweet) {
        super(layer, null, coordinate, defaultMarkerSize, STYLE.FIXED, getDefaultStyle());
        this.image = Util.imageFromURL(imageUrl);
        this.tweet = tweet;
        this.imageUrl = imageUrl;

        setColor(Color.BLACK);
        setBackColor(color);
    }

    public String getTweet() {
        return this.tweet;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    //Avatar with image on the map
    @Override
    public void paint(Graphics graphics, Point position, int radius) {
        int size = radius * 2;
        if (graphics instanceof Graphics2D && this.getBackColor() != null) {
            Graphics2D graphics2D = (Graphics2D) graphics;
            Composite oldComposite = graphics2D.getComposite();
            graphics2D.setComposite(AlphaComposite.getInstance(3));
            graphics2D.setPaint(this.getBackColor());
            graphics2D.setComposite(oldComposite);

            graphics.fillOval(position.x - radius, position.y - radius, size, size);
            graphics.drawImage(image, position.x - 10, position.y - 10, 20,20,null);
        }
    }

}
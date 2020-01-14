package BoardComponents;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by Lev on 20.04.14.
 */
public class ImageSlider extends JSlider {

    public ImageSlider(final ImageContainer image) {
        super(0, 500, 100);
        this.setMajorTickSpacing(50);
        this.setPaintTicks(true);
        this.setPaintLabels(true);

        this.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println(getValue());
                image.setImageWidth((int)(getValue() / 100.0 * image.getImageWidth()));
                image.setImageHeight((int) (getValue() / 100.0 * image.getImageHeight()));
                int width = image.getImageWidth();
                int height = image.getImageHeight();
                if (image.getImageWidth() < image.getScroll().getWidth()) {
                    width = image.getContainer().getCompWidth();
                }
                if (image.getImageHeight() < image.getScroll().getHeight()) {
                    height = image.getContainer().getCompHeight();
                }
                image.setPreferredSize(new Dimension(width, height));
                image.revalidate();
            }
        });
    }


}

package pipe.views;

import org.jfree.util.ShapeUtilities;
import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
import pipe.historyActions.HistoryItem;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class TransitionView extends ConnectableView<Transition> {
    public boolean _highlighted;

    private boolean _enabled;

    public TransitionView(Transition model, PetriNetController controller, Container parent, MouseInputAdapter transitionHandler, MouseInputAdapter animationHandler) {
        super(model.getId(), model, controller, parent, new Rectangle2D.Double(0, 0, model.getWidth(),
                model.getHeight()));
        setChangeListener();

        _enabled = false;
        _highlighted = false;

        rotate(model.getAngle());
        //TODO: DEBUG WHY CANT CALL THIS IN CONSTRUCTOR
        //        changeToolTipText();

        setMouseListener(transitionHandler, animationHandler);

    }

    private void setMouseListener(MouseInputAdapter transitionHandler, MouseInputAdapter animationHandler) {
        addMouseListener(transitionHandler);
        addMouseMotionListener(transitionHandler);
        addMouseWheelListener(transitionHandler);

        addMouseListener(animationHandler);

    }


    public void rotate(int angleInc) {
        ShapeUtilities.rotateShape(shape, Math.toRadians(angleInc), new Double(model.getCentre().getX()).floatValue(), new Double(model.getCentre().getY()).floatValue());
//        shape.transform(AffineTransform.getRotateInstance(Math.toRadians(angleInc), model.getHeight() / 2,
//                model.getHeight() / 2));
    }

    private void setChangeListener() {
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals(Transition.PRIORITY_CHANGE_MESSAGE) || name.equals(Transition.RATE_CHANGE_MESSAGE)) {
                    repaint();
                } else if (name.equals(Transition.ANGLE_CHANGE_MESSAGE) || name.equals(Transition.TIMED_CHANGE_MESSAGE)
                        || name.equals(Transition.INFINITE_SEVER_CHANGE_MESSAGE)) {
                    repaint();
                } else if (name.equals(Transition.ENABLED_CHANGE_MESSAGE) || name.equals(
                        Transition.DISABLED_CHANGE_MESSAGE)) {
                    repaint();
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return _enabled;
    }

    @Override
    public void setEnabled(boolean status) {
        if (_enabled && !status) {
//            _delayValid = false;
        }

        _enabled = status;

    }

    public void update() {
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected() && !_ignoreSelection) {
            g2.setColor(GUIConstants.SELECTION_FILL_COLOUR);
        } else {
            g2.setColor(GUIConstants.ELEMENT_FILL_COLOUR);
        }

        if (model.isTimed()) {
            if (model.isInfiniteServer()) {
                for (int i = 2; i >= 1; i--) {
                    g2.translate(2 * i, -2 * i);
                    g2.fill(shape);
                    Paint pen = g2.getPaint();
                    if (highlightView()) {
                        g2.setPaint(GUIConstants.ENABLED_TRANSITION_COLOUR);
                    } else if (isSelected() && !_ignoreSelection) {
                        g2.setPaint(GUIConstants.SELECTION_LINE_COLOUR);
                    } else {
                        g2.setPaint(GUIConstants.ELEMENT_LINE_COLOUR);
                    }
                    g2.draw(shape);
                    g2.setPaint(pen);
                    g2.translate(-2 * i, 2 * i);
                }
            }
            g2.fill(shape);
        }

        if (highlightView()) {
            g2.setPaint(GUIConstants.ENABLED_TRANSITION_COLOUR);
        } else if (isSelected() && !_ignoreSelection) {
            g2.setPaint(GUIConstants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(GUIConstants.ELEMENT_LINE_COLOUR);
        }

        g2.draw(shape);
        if (!model.isTimed()) {
            if (model.isInfiniteServer()) {
                for (int i = 2; i >= 1; i--) {
                    g2.translate(2 * i, -2 * i);
                    Paint pen = g2.getPaint();
                    g2.setPaint(GUIConstants.ELEMENT_FILL_COLOUR);
                    g2.fill(shape);
                    g2.setPaint(pen);
                    g2.draw(shape);
                    g2.translate(-2 * i, 2 * i);
                }
            }
            g2.draw(shape);
            g2.fill(shape);
        }
//        changeToolTipText();
    }

    @Override
    void setCentre(double x, double y) {
        super.setCentre(x, y);
        update();
    }

    @Override
    public void toggleAttributesVisible() {
        _attributesVisible = !_attributesVisible;
    }

    public boolean isInfiniteServer() {
        return model.isInfiniteServer();
    }

    public boolean isTimed() {
        return model.isTimed();
    }

    public int getPriority() {
        return model.
                getPriority();
    }

    /**
     * @return true if in animate mode and the model is enabled
     */
    private boolean highlightView() {
        return model.isEnabled() && petriNetController.isInAnimationMode();
    }

    @Override
    public void addToContainer(Container container) {
        addLabelToContainer(container);
    }

    public int getAngle() {
        return model.getAngle();
    }

    @Override
    public boolean contains(int x, int y) {
        return shape.contains(x,y);
    }

    public void setModel(Transition model) {
        this.model = model;
    }

    //TODO: DELETE
    public HistoryItem groupTransitions() {
        return null;
    }

}

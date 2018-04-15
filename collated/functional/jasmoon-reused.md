# jasmoon-reused
###### \java\seedu\address\ui\ZoomPane.java
``` java
    //https://stackoverflow.com/questions/16680295/javafx-correct-scaling

    /**
     * method for create ZoomPane
     * @param group
     * @return a ZoomPane with Parent as datatype
     */
    public Parent createZoomPane(Group group) {
        final double scaleDelta = 1.1;
        // Create a zoom pane for zoom in/out
        final StackPane zoomPane = new StackPane();
        zoomPane.getChildren().add(group);
        final Group zoomContent = new Group(zoomPane);
        // Create a pane for holding the content, when the content is smaller than the view port,
        // it will stay the view port size, make sure the content is centered
        final StackPane canvasPane = new StackPane();
        canvasPane.setStyle("-fx-background-color: #434547;");
        canvasPane.getChildren().add(zoomContent);

        final ScrollPane scroller = new ScrollPane();

        final Group scrollContent = new Group(canvasPane);
        // Scroll pane for scrolling
        scroller.setContent(scrollContent);

        scroller.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable,
                                Bounds oldValue, Bounds newValue) {
                zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
            }
        });

        scroller.setPrefViewportWidth(800);
        scroller.setPrefViewportHeight(800);
        zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                event.consume();

                if (event.getDeltaY() == 0) {
                    return;
                }

                double scaleFactor = (event.getDeltaY() > 0) ? scaleDelta
                        : 1 / scaleDelta;

                // amount of scrolling in each direction in scrollContent coordinate
                // units
                Point2D scrollOffset = figureScrollOffset(scrollContent, scroller);

                group.setScaleX(group.getScaleX() * scaleFactor);
                group.setScaleY(group.getScaleY() * scaleFactor);

                // move viewport so that old center remains in the center after the
                // scaling
                repositionScroller(scrollContent, scroller, scaleFactor, scrollOffset);

            }
        });

        // Panning via drag....
        final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();
        scrollContent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
            }
        });

        scrollContent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double deltaX = event.getX() - lastMouseCoordinates.get().getX();
                double extraWidth = scrollContent.getLayoutBounds().getWidth()
                        - scroller.getViewportBounds().getWidth();
                double deltaH = deltaX * (scroller.getHmax() - scroller.getHmin()) / extraWidth;
                double desiredH = scroller.getHvalue() - deltaH;
                scroller.setHvalue(Math.max(0, Math.min(scroller.getHmax(), desiredH)));

                double deltaY = event.getY() - lastMouseCoordinates.get().getY();
                double extraHeight = scrollContent.getLayoutBounds().getHeight()
                        - scroller.getViewportBounds().getHeight();
                double deltaV = deltaY * (scroller.getHmax() - scroller.getHmin()) / extraHeight;
                double desiredV = scroller.getVvalue() - deltaV;
                scroller.setVvalue(Math.max(0, Math.min(scroller.getVmax(), desiredV)));
            }
        });

        return scroller;
    }

    /**
     * Calculates the offset for zooming.
     * @param scrollContent
     * @param scroller
     * @return the point of the zoom.
     */
    private Point2D figureScrollOffset(Node scrollContent, ScrollPane scroller) {
        double extraWidth = scrollContent.getLayoutBounds().getWidth()
                - scroller.getViewportBounds().getWidth();
        double hScrollProportion = (scroller.getHvalue() - scroller.getHmin())
                / (scroller.getHmax() - scroller.getHmin());
        double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        double extraHeight = scrollContent.getLayoutBounds().getHeight()
                - scroller.getViewportBounds().getHeight();
        double vScrollProportion = (scroller.getVvalue() - scroller.getVmin())
                / (scroller.getVmax() - scroller.getVmin());
        double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        return new Point2D(scrollXOffset, scrollYOffset);
    }

    /**
     * Positions the viewport at the right zoom.
     * @param scrollContent
     * @param scroller
     * @param scaleFactor
     * @param scrollOffset
     */
    private void repositionScroller(Node scrollContent, ScrollPane scroller,
                                    double scaleFactor, Point2D scrollOffset) {
        double scrollXOffset = scrollOffset.getX();
        double scrollYOffset = scrollOffset.getY();
        double extraWidth = scrollContent.getLayoutBounds().getWidth()
                - scroller.getViewportBounds().getWidth();
        if (extraWidth > 0) {
            double halfWidth = scroller.getViewportBounds().getWidth() / 2;
            double newScrollXOffset = (scaleFactor - 1) *  halfWidth + scaleFactor * scrollXOffset;
            scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax()
                    - scroller.getHmin()) / extraWidth);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
        double extraHeight = scrollContent.getLayoutBounds().getHeight()
                - scroller.getViewportBounds().getHeight();
        if (extraHeight > 0) {
            double halfHeight = scroller.getViewportBounds().getHeight() / 2;
            double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
            scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax()
                    - scroller.getVmin()) / extraHeight);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
    }
}
```

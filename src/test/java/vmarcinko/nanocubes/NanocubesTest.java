package vmarcinko.nanocubes;

import vmarcinko.nanocubes.quadtree.QuadTreeConverter;

import java.util.ArrayList;
import java.util.List;

public class NanocubesTest {
    public static void main(String[] args) {
        Schema<ExampleDataPoint> schema = prepareSchema();
        LabellingFn<ExampleDataPoint> timeLabellingFn = prepareTimeLabellingFn();
        Nanocube<ExampleDataPoint> nanocube = new Nanocube<>(schema, timeLabellingFn);

        List<ExampleDataPoint> dataPoints = prepareDataPoints();
        for (ExampleDataPoint dataPoint : dataPoints) {
            nanocube.insert(dataPoint);
        }

        System.out.println("nanocube = " + nanocube.toPrettyString());
    }

    private static List<ExampleDataPoint> prepareDataPoints() {
        List<ExampleDataPoint> list = new ArrayList<>();
        list.add(new ExampleDataPoint(1, 2, ExampleDataPoint.DeviceType.ANDROID, 0));
//        list.add(new ExampleDataPoint(1, 2, ExampleDataPoint.DeviceType.IPHONE, 0));
//        list.add(new ExampleDataPoint(2, 1, ExampleDataPoint.DeviceType.IPHONE, 0));
//        list.add(new ExampleDataPoint(2, 2, ExampleDataPoint.DeviceType.ANDROID, 0));
//        list.add(new ExampleDataPoint(3, 1, ExampleDataPoint.DeviceType.IPHONE, 0));
        return list;
    }

    private static Schema<ExampleDataPoint> prepareSchema() {
        Schema<ExampleDataPoint> schema = new Schema<>();

        List<LabellingFn<ExampleDataPoint>> geoChain = schema.addChain();
        geoChain.add(createGeoLabellingFn(1));
        geoChain.add(createGeoLabellingFn(2));

        List<LabellingFn<ExampleDataPoint>> deviceTypeChain = schema.addChain();
        deviceTypeChain.add(createDeviceTypeLabellingFn());

        return schema;
    }

    private static LabellingFn<ExampleDataPoint> createGeoLabellingFn(final int depth) {
        return new LabellingFn<ExampleDataPoint>() {
            @Override
            public Object label(ExampleDataPoint dataPoint) {
                return QuadTreeConverter.convert(dataPoint.getGeoX(), dataPoint.getGeoY(), 3, 3, depth);
            }
        };
    }

    private static LabellingFn<ExampleDataPoint> createDeviceTypeLabellingFn() {
        return new LabellingFn<ExampleDataPoint>() {
            @Override
            public Object label(ExampleDataPoint dataPoint) {
                return dataPoint.getDeviceType();
            }
        };
    }

    private static LabellingFn<ExampleDataPoint> prepareTimeLabellingFn() {
        return new LabellingFn<ExampleDataPoint>() {
            @Override
            public Object label(ExampleDataPoint dataPoint) {
                return dataPoint.getTime();
            }
        };
    }
}

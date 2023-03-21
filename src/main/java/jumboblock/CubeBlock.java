package jumboblock;

import io.github.smile_ns.simplejson.SimpleJson;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CubeBlock implements JumboBlock {

    private final int ROTATE;
    private final String TYPE;

    public CubeBlock(int rotate, String type) {
        ROTATE = rotate;
        TYPE = type;
    }

    /*
    -------
    -----0-
    ---2-1-
    -4-3---
    -5-----
    -------
     */
    private Map<String, List<Integer>> getRenderedTextures() throws IOException {
        SimpleJson model = new SimpleJson(
                new File("./models/" + TYPE + ".json"));

        Map<String, List<Integer>> txMap = new HashMap<>();
        for (String key : model.getKeySet("textures")) {
            String basename = model.getString("textures." + key);
            String tx = basename.substring(basename.lastIndexOf('/'));
            Integer[] clonedFaces =
                    key.equals("all") || key.equals("pattern") || key.equals("texture") ? new Integer[]{0, 1, 2, 3, 4, 5} :
                    key.equals("side") ? new Integer[]{0, 2, 3, 5} :
                    key.equals("end") ? new Integer[]{1, 4} :
                    key.equals("north") ? new Integer[]{0} :
                    key.equals("down") ? new Integer[]{1} :
                    key.equals("west") ? new Integer[]{2} :
                    key.equals("south") ? new Integer[]{3} :
                    key.equals("up") ? new Integer[]{4} :
                    key.equals("east") ? new Integer[]{5} :
                            new Integer[]{};
            
            List<Integer> faceLst = txMap.containsKey(tx) ? txMap.get(tx) : new ArrayList<>();
            faceLst.addAll(Arrays.asList(clonedFaces));
            txMap.put(tx, faceLst);
        }

        return txMap;
    }

    @Override
    public void output() throws IOException {
        long s = System.currentTimeMillis();

        Map<String, List<Integer>> txMap = getRenderedTextures();

        String[] viewMap = new String[16 * 16 * 6];
        for (Map.Entry<String, List<Integer>> e : txMap.entrySet()) {
            String tx = e.getKey();

            for (Integer v : e.getValue()) {
                List<String> faceTypes =
                        v == 0 ? new ArrayList<>(Arrays.asList("north", "side")) :
                        v == 1 ? new ArrayList<>(Arrays.asList("down", "end")) :
                        v == 2 ? new ArrayList<>(Arrays.asList("west", "side")) :
                        v == 3 ? new ArrayList<>(Arrays.asList("south", "side")) :
                        v == 4 ? new ArrayList<>(Arrays.asList("up", "end")) :
                        v == 5 ? new ArrayList<>(Arrays.asList("east", "side")) :
                        new ArrayList<>();

                faceTypes.add("all");
                faceTypes.add("pattern");
                faceTypes.add("texture");

                String[] bitMap = JumboBlockUtils.getBlockBitMap(tx, faceTypes);
                System.arraycopy(bitMap, 0, viewMap, v * 16 * 16, bitMap.length);
            }
        }

        SimpleJson mdJson = new SimpleJson(new File("./jumbo_models/" + TYPE + ".json"));
        mdJson.put("normal", viewMap);
        mdJson.save();
        long e = System.currentTimeMillis();
        System.out.println(mdJson.getFile().getPath() + " Time: " + (e - s) + "ms");
    }
}

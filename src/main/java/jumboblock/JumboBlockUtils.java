package jumboblock;

import io.github.smile_ns.simplejson.SimpleJson;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class JumboBlockUtils {
    public static SimpleJson colorJson;
    private static Set<String> directKeys;
    private static final Map<Integer, List<PixelBlock>> pixelMemory = new HashMap<>();
    public static List<String> faceTypeList = new ArrayList<>(Arrays.asList(
            "all", "end", "side", "top", "bottom",
            "front", "pattern", "texture", "north", "south",
            "east", "west", "up", "down", "back"
    ));

    static {
        try {
            colorJson = new SimpleJson(new File("./color.json"));
            directKeys = colorJson.toMap().keySet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static String[] getBlockBitMap(String tx, List<String> faceTypes) throws IOException {
        String[] bitMap = new String[16 * 16];
        BufferedImage bi = ImageIO.read(
                new File("./textures/" + tx + ".png"));

        for (int i = 0;i < 16;i++) {
            for (int j = 0;j < 16;j++) {
                Color pixel = new Color(bi.getRGB(i, j));
                String pxlBlock = getPixelBlock(faceTypes, pixel);
                bitMap[i + (j * 16)] = pxlBlock;
            }
        }

        /*
        for (Map.Entry<Integer, List<PixelBlock>> el : pixelMemory.entrySet()) {
            System.out.println(el.getKey() + ": ");
            for (PixelBlock p : el.getValue()) {
                System.out.println(p.blockName);
            }
        }
         */
        return bitMap;
    }

    //15150 14541
    protected static String getPixelBlock(List<String> faceTypes, Color color) {
        int rgb = color.getRGB();
        if (pixelMemory.containsKey(rgb)) {
            List<PixelBlock> pList = pixelMemory.get(rgb);
            for (PixelBlock p : pList) {
                for (String t : faceTypes) {
                    if (t.equals(p.faceType)) return p.blockName;
                }
            }
        }

        List<PixelBlock> allPixelList = new ArrayList<>();
        int r1 = color.getRed(), g1 = color.getGreen(), b1 = color.getBlue();
        for (String blockName : directKeys) {
            for (String face : colorJson.getKeySet(blockName)) {
                Color c = new Color(colorJson.getInt(blockName + "." + face));
                int r2 = c.getRed(), g2 = c.getGreen(), b2 = c.getBlue();
                int dis = (int) (Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2));

                allPixelList.add(new PixelBlock(face, blockName, dis));
            }
        }

        allPixelList.sort(Comparator.comparingInt(p -> p.dis));
        List<PixelBlock> pList = new ArrayList<>();
        List<String> allFaceTypes = new ArrayList<>(
                Arrays.asList("north", "west", "down", "east", "south",
                "up", "side", "end", "all", "pattern", "texture"));

        for (PixelBlock p : allPixelList) {
            if (allFaceTypes.contains(p.faceType)) {
                pList.add(p);
                allFaceTypes.remove(p.faceType);
            }
        }

        pixelMemory.put(rgb, pList);

        return getPixelBlock(faceTypes, color);
    }

    static class PixelBlock {
        String faceType;
        String blockName;
        int dis;

        PixelBlock(String faceType, String blockName, int dis) {
            this.faceType = faceType;
            this.blockName = blockName;
            this.dis = dis;
        }
    }

        /*
    ---N---
    ---0---
    -1-2-3-
    ---4---
    ---5---
     */
    /*
    public static int getGenericFace(Block block) {
        BlockData data = block.getBlockData();
        String[] dataArr = data.getAsString().split("[\\[,=\\]]");

        String face = "";
        for (int i = 0;i < dataArr.length;i++) {
            if (dataArr[i].equals("facing") || dataArr[i].equals("axis")) {
                face = dataArr[i + 1];
                break;
            }
        }

        return
                face.equals("up") || face.equals("y") ? 5 :
                face.equals("east") || face.equals("x") ? 3 :
                face.equals("west") ? 1 :
                face.equals("south") ? 4 :
                face.equals("down") ? 2 :
                0;
    }
     */
}

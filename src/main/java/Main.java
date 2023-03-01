import io.github.smile_ns.simplejson.SimpleJson;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main {

    static List<String> parentList = new ArrayList<>(Arrays.asList(
            "minecraft:block/leaves", "minecraft:block/cube_column", "minecraft:block/cube_column_horizontal",
            "minecraft:block/cube_all", "minecraft:block/cube_bottom_top", "minecraft:block/cube_mirrored_all",
            "minecraft:block/orientable_with_bottom", "minecraft:block/template_glazed_terracotta", "minecraft:block/template_single_face",
            "minecraft:block/cube", "minecraft:block/template_command_block", "block/cube",
            "block/cube_mirrored", "minecraft:block/cube_directional", "minecraft:block/cube_top",
            "block/template_single_face", "block/observer", "block/orientable_with_bottom",
            "block/cube_column", "minecraft:block/template_seagrass", "block/cube_directional"
    ));

    public static void main(String[] args) throws IOException {
        SimpleJson json = new SimpleJson(new File("./output.json"));
        Map<String, Object> map = json.toMap();

        File[] models = new File("./models/").listFiles();

        assert models != null;
        for (File md : models) {
            SimpleJson mdJson = new SimpleJson(md);
            if (mdJson.containsNode("parent")) {
                String parent = mdJson.getString("parent");
                if (!parentList.contains(parent)) continue;
                if (!parent.equals("minecraft:block/cube_all")) continue;

                //List<BufferedImage> textures = new ArrayList<>();
                String path = "textures.all";
                if (mdJson.containsNode(path)) {
                    String basename = mdJson.getString(path);
                    if (!basename.contains("minecraft:block/")) continue;
                    String tx = basename.substring(basename.lastIndexOf('/'));
                    BufferedImage bi = ImageIO.read(new File("textures/" + tx + ".png"));
                    SimpleJson jumboJson = new SimpleJson(new File("jumbo_textures/" + tx + ".json"));

                    for (int i = 0;i < bi.getWidth();i++) {
                        for (int j = 0;j < bi.getHeight();j++) {
                            Color c1 = new Color(bi.getRGB(i, j));
                            int r1 = c1.getRed();
                            int g1 = c1.getGreen();
                            int b1 = c1.getBlue();

                            int minDis = 195075;
                            String pxlBlock = "";
                            for (Map.Entry<String, Object> e : map.entrySet()) {
                                Color c2 = new Color((Integer) e.getValue());
                                int r2 = c2.getRed();
                                int g2 = c2.getGreen();
                                int b2 = c2.getBlue();

                                int dis = (int) (Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2));
                                if (dis < minDis) {
                                    minDis = dis;
                                    pxlBlock = e.getKey();
                                }
                            }

                            System.out.println(minDis);
                            jumboJson.put("(" + i + ", " + j + ")", pxlBlock);
                        }
                    }

                    jumboJson.save();
                }
            }
        }
    }

    static void generateColorJson() throws IOException {
        List<String> faceTypeLst = new ArrayList<>(Arrays.asList(
                "all", "end", "side", "top", "bottom",
                "front", "pattern", "texture", "north", "south",
                "east", "west", "up", "down", "back"
        ));

        File[] models = new File("./models/").listFiles();
        SimpleJson json = new SimpleJson(new File("./output.json"));

        assert models != null;
        for (File md : models) {
            SimpleJson mdJson = new SimpleJson(md);
            SimpleJson faces = new SimpleJson();

            if (!mdJson.containsNode("parent")) continue;
            String parent = mdJson.getString("parent");
            if (!parentList.contains(parent)) continue;

            for (String faceType : faceTypeLst) {
                String path = "textures." + faceType;
                if (!mdJson.containsNode(path)) continue;
                String basename = mdJson.getString(path);
                if (!basename.contains("minecraft:block/")) continue;

                String txName = basename.substring(basename.lastIndexOf('/'));
                BufferedImage bi = ImageIO.read(new File("textures/" + txName + ".png"));

                int r = 0, g = 0, b = 0;
                for (int i = 0;i < bi.getWidth();i++) {
                    for (int j = 0;j < bi.getHeight();j++) {
                        Color c = new Color(bi.getRGB(i, j));
                        if (c.getAlpha() != 0) {
                            r += c.getRed();
                            g += c.getGreen();
                            b += c.getBlue();
                        }
                    }
                }

                int pixel = bi.getWidth() * bi.getHeight();

                Color color = new Color(r / pixel, g / pixel, b / pixel);
                faces.put(faceType, color.getRGB());
            }

            String filename = md.getName();
            String name = filename.substring(0, filename.lastIndexOf('.'));
            json.put(name, faces);
        }

        json.save();
    }

    static void analyzeParents() throws IOException{
        File[] models = new File("./models/").listFiles();

        Set<String> parents = new HashSet<>();
        assert models != null;
        for (File md : models) {
            SimpleJson json = new SimpleJson(md);
            if (json.containsNode("parent")) {
                String parent = json.getString("parent");
                if (parents.add(parent)) {
                    System.out.println(md + ": " + parent);
                }
            }
        }
    }
}

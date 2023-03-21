import io.github.smile_ns.simplejson.SimpleJson;
import jumboblock.CubeBlock;
import jumboblock.JumboBlockUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        long s = System.currentTimeMillis();
        /*
        for (String key : JumboBlockUtils.colorJson.toMap().keySet()) {
            new CubeBlock(0, key).output();
        }
         */
        new CubeBlock(0, "crafting_table").output();

        long e = System.currentTimeMillis();

        System.out.println("Total Time: " + (e - s) + "ms");

        //generateColorJson();
        //analyzeParents();
    }

    static void generateColorJson() throws IOException {
        List<String> parentList = new ArrayList<>(Arrays.asList(
                "minecraft:block/leaves", "minecraft:block/cube_column", "minecraft:block/cube_column_horizontal",
                "minecraft:block/cube_all", "minecraft:block/cube_bottom_top", "minecraft:block/cube_mirrored_all",
                "minecraft:block/orientable_with_bottom", "minecraft:block/template_glazed_terracotta", "minecraft:block/template_single_face",
                "minecraft:block/cube", "minecraft:block/template_command_block", "block/cube",
                "block/cube_mirrored", "minecraft:block/cube_directional", "minecraft:block/cube_top",
                "block/template_single_face", "block/observer", "block/orientable_with_bottom",
                "block/cube_column", "minecraft:block/template_seagrass", "block/cube_directional",
                "block/block"
        ));
        
        File[] models = new File("./models/").listFiles();
        SimpleJson json = new SimpleJson(new File("./color.json"));

        assert models != null;
        for (File md : models) {
            SimpleJson mdJson = new SimpleJson(md);
            SimpleJson faces = new SimpleJson();

            if (!mdJson.containsNode("parent")) continue;
            String parent = mdJson.getString("parent");
            if (!parentList.contains(parent)) continue;

            if (!mdJson.containsNode("textures")) continue;
            for (String faceType : mdJson.getKeySet("textures")) {
                if (!JumboBlockUtils.faceTypeList.contains(faceType)) continue;
                String path = "textures." + faceType;
                String basename = mdJson.getString(path);
                if (basename.contains("#")) continue;
                String txName = basename.substring(basename.lastIndexOf('/'));
                BufferedImage bi = ImageIO.read(new File("textures/" + txName + ".png"));

                int r = 0, g = 0, b = 0;
                for (int i = 0;i < 16;i++) {
                    for (int j = 0;j < 16;j++) {
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

            if (faces.size() != 0) {
                String filename = md.getName();
                String name = filename.substring(0, filename.lastIndexOf('.'));
                json.put(name, faces);
            }
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

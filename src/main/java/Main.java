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

        List<Map.Entry<String, Object>> list = new ArrayList<>(map.entrySet());
        list.sort(Comparator.comparing((Map.Entry<String, Object> obj) -> ((Integer) obj.getValue())));


        for (Map.Entry<String, Object> e : list) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }
    }

    static void generateMonoJson() throws IOException {
        List<String> txTypeList = new ArrayList<>(Arrays.asList(
                "all", "end", "side", "top", "bottom",
                "front", "pattern", "texture", "north", "south",
                "east", "west", "up", "down", "back"
        ));

        File[] models = new File("./models/").listFiles();
        SimpleJson json = new SimpleJson(new File("./output.json"));

        assert models != null;
        for (File md : models) {
            SimpleJson mdJson = new SimpleJson(md);
            if (mdJson.containsNode("parent")) {
                String parent = mdJson.getString("parent");
                if (!parentList.contains(parent)) continue;

                List<BufferedImage> textures = new ArrayList<>();

                for (String type : txTypeList) {
                    String path = "textures." + type;
                    if (mdJson.containsNode(path)) {
                        String basename = mdJson.getString(path);
                        if (!basename.contains("minecraft:block/")) continue;
                        String block = basename.substring(basename.lastIndexOf('/'));
                        BufferedImage bi = ImageIO.read(new File("images/" + block + ".png"));
                        textures.add(bi);
                    }
                }

                if (textures.size() == 0) continue;

                int sum1 = 0;
                for (BufferedImage bi : textures) {
                    int sum2 = 0;
                    for (int i = 0;i < bi.getWidth();i++) {
                        for (int j = 0;j < bi.getHeight();j++) {
                            int c = bi.getRGB(i, j);
                            Color color = new Color(c);
                            if (color.getAlpha() != 0) {
                                sum2 += c;
                            }
                        }
                    }

                    sum1 += (sum2 / (bi.getWidth() * bi.getHeight()));
                }

                int mono = sum1 / textures.size();
                String filename = md.getName();
                String name = filename.substring(0, filename.lastIndexOf('.'));
                System.out.println(name + ": " + mono);
                json.put(name, mono);
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

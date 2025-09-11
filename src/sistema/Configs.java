package sistema;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 *
 * @author Bonn
 */
public class Configs {

    public static Path path = Path.of("Arquivos/configuracoes.txt");
    private static final Field FIELDS[] = sistema.Info.class.getDeclaredFields();

    public static void carregar() throws Exception {

        if (!new java.io.File(path.toUri()).exists()) {
            new java.io.File(path.toUri()).mkdirs();
            new java.io.File(path.toUri()).createNewFile();
        } else {
            BufferedReader ler = Files.newBufferedReader(path);
            Properties prop = new Properties();
            prop.load(ler);
            for (int i = 0; i < FIELDS.length; i++) {
                if (!Modifier.isFinal(FIELDS[i].getModifiers()) && !FIELDS[i].getName().startsWith("_")) {
                    FIELDS[i].setAccessible(true);
                    Class c = FIELDS[i].getType();
                    if (c == int.class) {
                        FIELDS[i].set(sistema.Info.class, Integer.valueOf(prop.getProperty(FIELDS[i].getName())));
                    } else if (c == byte.class) {
                        FIELDS[i].set(sistema.Info.class, Byte.valueOf(prop.getProperty(FIELDS[i].getName())));
                    } else if (c == float.class) {
                        FIELDS[i].set(sistema.Info.class, Float.valueOf(prop.getProperty(FIELDS[i].getName())));
                    } else if (c == boolean.class) {
                        FIELDS[i].set(sistema.Info.class, Boolean.valueOf(prop.getProperty(FIELDS[i].getName())));
                    } else if (c == short.class) {
                        FIELDS[i].set(sistema.Info.class, Short.valueOf(prop.getProperty(FIELDS[i].getName())));
                    } else if (c == double.class) {
                        FIELDS[i].set(sistema.Info.class, Double.valueOf(prop.getProperty(FIELDS[i].getName())));
                    } else {
                        FIELDS[i].set(sistema.Info.class, prop.getProperty(FIELDS[i].getName()));
                    }

                }
            }
            ler.close();
        }
        
        salvar();
    }

    public static void salvar() {
        try {
            BufferedWriter esc = Files.newBufferedWriter(path);
            Properties prop = new Properties();

            for (int i = 0; i < FIELDS.length; i++) {
                Object obj = FIELDS[i].get(sistema.Info.class);
                if (!Modifier.isFinal(FIELDS[i].getModifiers()) && !FIELDS[i].getName().startsWith("_")) {
                    prop.setProperty(FIELDS[i].getName(), String.valueOf(obj));
                }

            }
            prop.store(esc, null);
            esc.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

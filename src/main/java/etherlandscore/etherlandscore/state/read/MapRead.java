package etherlandscore.etherlandscore.state.read;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Set;

public interface MapRead {
    URL getImage_url();

    Set<Integer> getMapIDs();
}

package model.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static model.enums.NoteEnum.A;
import static model.enums.NoteEnum.B;
import static model.enums.NoteEnum.C;
import static model.enums.NoteEnum.D;
import static model.enums.NoteEnum.E;
import static model.enums.NoteEnum.F;
import static model.enums.NoteEnum.G;

/**
 * The set of major and minor keys with maps to their associated flats and sharps.
 */
public enum Key {
    C_SHARP_MAJOR,   // 7 sharps
    F_SHARP_MAJOR,   // 6 sharps
    B_MAJOR,         // 5 sharps
    E_MAJOR,         // 4 sharps
    A_MAJOR,         // 3 sharps
    D_MAJOR,         // 2 sharps
    G_MAJOR,         // 1 sharp
    C_MAJOR,
    F_MAJOR,         // 1 flat
    B_FLAT_MAJOR,    // 2 flats
    E_FLAT_MAJOR,    // 3 flats
    A_FLAT_MAJOR,    // 4 flats
    D_FLAT_MAJOR,    // 5 flats
    G_FLAT_MAJOR,    // 6 flats
    C_FLAT_MAJOR,    // 7 flats

    A_SHARP_MINOR,   // 7 sharps
    D_SHARP_MINOR,   // 6 sharps
    G_SHARP_MINOR,   // 5 sharps
    C_SHARP_MINOR,   // 4 sharps
    F_SHARP_MINOR,   // 3 sharps
    B_MINOR,         // 2 sharps
    E_MINOR,         // 1 sharp
    A_MINOR,
    D_MINOR,         // 1 flat
    G_MINOR,         // 2 flats
    C_MINOR,         // 3 flats
    F_MINOR,         // 4 flats
    B_FLAT_MINOR,    // 5 flats
    E_FLAT_MINOR,    // 6 flats
    A_FLAT_MINOR;    // 7 flats

    public static Map<Key, List<NoteEnum>> keyToFlats;
    static {
        keyToFlats = new HashMap<>();
        keyToFlats.put(C_FLAT_MAJOR, Arrays.asList(B, E, A, D, G, C, F));
        keyToFlats.put(G_FLAT_MAJOR, Arrays.asList(B, E, A, D, G, C));
        keyToFlats.put(D_FLAT_MAJOR, Arrays.asList(B, E, A, D, G));
        keyToFlats.put(A_FLAT_MAJOR, Arrays.asList(B, E, A, D));
        keyToFlats.put(E_FLAT_MAJOR, Arrays.asList(B, E, A));
        keyToFlats.put(B_FLAT_MAJOR, Arrays.asList(B, E));
        keyToFlats.put(F_MAJOR, Arrays.asList(B));
        keyToFlats.put(C_MAJOR, Arrays.asList());
        keyToFlats.put(A_FLAT_MINOR, Arrays.asList(B, E, A, D, G, C, F));
        keyToFlats.put(E_FLAT_MINOR, Arrays.asList(B, E, A, D, G, C));
        keyToFlats.put(B_FLAT_MINOR, Arrays.asList(B, E, A, D, G));
        keyToFlats.put(F_MINOR, Arrays.asList(B, E, A, D));
        keyToFlats.put(C_MINOR, Arrays.asList(B, E, A));
        keyToFlats.put(G_MINOR, Arrays.asList(B, E));
        keyToFlats.put(D_MINOR, Arrays.asList(B));
        keyToFlats.put(A_MINOR, Arrays.asList());
    }

    public static Map<Key, List<NoteEnum>> keyToSharps;
    static {
        keyToSharps = new HashMap<>();
        keyToSharps.put(C_SHARP_MAJOR, Arrays.asList(F, C, G, D, A, E, B));
        keyToSharps.put(F_SHARP_MAJOR, Arrays.asList(F, C, G, D, A, E));
        keyToSharps.put(B_MAJOR, Arrays.asList(F, C, G, D, A));
        keyToSharps.put(E_MAJOR, Arrays.asList(F, C, G, D));
        keyToSharps.put(A_MAJOR, Arrays.asList(F, C, G));
        keyToSharps.put(D_MAJOR, Arrays.asList(F, C));
        keyToSharps.put(G_MAJOR, Arrays.asList(F));
        keyToFlats.put(C_MAJOR, Arrays.asList());
        keyToSharps.put(A_SHARP_MINOR, Arrays.asList(F, C, G, D, A, E, B));
        keyToSharps.put(D_SHARP_MINOR, Arrays.asList(F, C, G, D, A, E));
        keyToSharps.put(G_SHARP_MINOR, Arrays.asList(F, C, G, D, A));
        keyToSharps.put(C_SHARP_MINOR, Arrays.asList(F, C, G, D));
        keyToSharps.put(F_SHARP_MINOR, Arrays.asList(F, C, G));
        keyToSharps.put(B_MINOR, Arrays.asList(F, C));
        keyToSharps.put(E_MINOR, Arrays.asList(F));
        keyToFlats.put(A_MINOR, Arrays.asList());
    }
}

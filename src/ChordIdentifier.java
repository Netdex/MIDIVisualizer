import java.util.ArrayList;
import java.util.HashMap;

public class ChordIdentifier {

	private HashMap<String, String> chordMap = new HashMap<String, String>();

	public ChordIdentifier() {
		chordMap.put("1,5,8", "");
		chordMap.put("1,4,8", "m");
		chordMap.put("1,5,9", ">+(5)");
		chordMap.put("1,4,7", ">0");
		chordMap.put("1,5,7", ">-5");
		chordMap.put("1,4,9", "m>+(5)");
		chordMap.put("3,5,8", ">add9");
		chordMap.put("3,5,8", ">sus2");
		chordMap.put("3,4,8", "m>add9");
		chordMap.put("3,4,8", "m>sus2");
		chordMap.put("4,5,11", "(7)>+9");
		chordMap.put("1,6,8", ">sus(4)");
		chordMap.put("1,6,8", "m>sus(4)");
		chordMap.put("5,10,11", "13");
		chordMap.put("5,9,11", "-13");
		chordMap.put("1,5,8,11", "7");
		chordMap.put("3,5,8,11", "9");
		chordMap.put("1,5,8,10", "6");
		chordMap.put("1,4,8,11", "m7");
		chordMap.put("1,4,8,10", "m6");
		chordMap.put("3,4,8,11", "m9");
		chordMap.put("1,5,8,12", "maj7");
		chordMap.put("1,4,8,12", "m(maj7)");
		chordMap.put("3,5,8,12", "maj9");
		chordMap.put("3,4,8,12", "m(maj9)");
		chordMap.put("2,5,8,11", "(7)>-9");
		chordMap.put("3,5,8,10", "6.9");
		chordMap.put("3,4,8,10", "m6.9");
		chordMap.put("1,6,8,11", "7>sus(4)");
		chordMap.put("1,6,8,11", "m7>sus(4)");
		chordMap.put("1,5,9,11", "7>+(5)");
		chordMap.put("1,5,7,11", "7>-5");
		chordMap.put("1,4,7,11", "m7>-5");
		chordMap.put("3,5,9,11", "9>+5");
		chordMap.put("3,5,9,11", ">-13.9");
		chordMap.put("3,5,7,11", "9>-5");
		chordMap.put("2,5,9,11", "(7)>-9+5");
		chordMap.put("2,5,7,11", "(7)>-9-5");
		chordMap.put("2,5,7,11", ">+11-9");
		chordMap.put("4,5,9,11", "(7)>+9+5");
		chordMap.put("4,5,7,11", "(7)>+9-5");
		chordMap.put("4,5,7,11", ">+11+9");
		chordMap.put("3,6,8,11", "11");
		chordMap.put("3,5,10,11", "13.9");
		chordMap.put("2,5,10,11", "13>-9");
		chordMap.put("2,5,9,11", ">-13-9");
		chordMap.put("3,6,10,11", "13.11(9)");
		chordMap.put("2,6,10,11", "13.11-9");
		chordMap.put("3,7,10,11", "13>+11(9)");
		chordMap.put("3,5,7,8,11", ">+11");
		chordMap.put("1,5,7,10,11", "13>-5");
		chordMap.put("1,5,7,9,11", ">-13-5");
		chordMap.put("1,5,7,9,11", ">+11+5");
		chordMap.put("2,5,7,10,11", "13>-9-5");
		chordMap.put("3,5,7,9,11", "9>+5-5");
	}

	public String[] getChordName(int[] notes) {
		if(notes.length < 2)
			return new String[0];
		boolean[] offset = new boolean[12];
		for (int i = 0; i < notes.length; i++) {
			int note = notes[i] % 12;
			offset[note] = true;
		}
		int c = 0;
		for (int i = 0; i < offset.length; i++) {
			if (offset[i])
				c++;
		}
		int cidx = 0;
		int[] res = new int[c];
		for (int i = 0; i < offset.length; i++) {
			if (offset[i])
				res[cidx++] = i;
		}
		
		if(res.length > 5)
			return new String[0];
		if(res.length == 1)
			return new String[]{intervalName(0)};
		if(res.length == 2){
			return new String[]{intervalName(Math.abs(res[0] - res[1]))
					,intervalName(12 - Math.abs(res[1] - res[0]))};
		}
		else{
		ArrayList<Integer[]> perms = new ArrayList<Integer[]>();
		permute(perms, res, 0);
		ArrayList<String> poss = new ArrayList<String>();
		for (Integer[] ia : perms) {
			int root = ia[0];
			int[] diff = new int[ia.length];
			for (int i = 0; i < ia.length; i++) {
				if (ia[i] < root)
					diff[i] = 12 - Math.abs(ia[i] - root);
				else
					diff[i] = ia[i] - root;
			}
			String key = "";
			for (int i = 0; i < diff.length; i++) {
				key += (diff[i] + 1) + ",";
			}
			key = key.substring(0, key.length() - 1);
			if (chordMap.get(key) != null){
				String keyname = PianoReceiver.getKeyName(root);
				keyname = keyname.substring(0, keyname.lastIndexOf("-"));
				poss.add("chord " + keyname + " " + chordMap.get(key));
			}
		}

		return poss.toArray(new String[0]);
		}
	}

	String intervalName(int diff){
		final String[] names =
			{
					"P1",
					"m2",
					"M2",
					"m3",
					"M3",
					"P4",
					"d5/A4",
					"P5",
					"m6",
					"m7",
					"M7",
					"P8"
					
			};
		return "interval " + names[diff];
	}
	void permute(ArrayList<Integer[]> result, int[] a, int k) {
		if (k == a.length)
			result.add(toObject(a));
		else {
			for (int i = k; i < a.length; i++) {
				int temp = a[k];
				a[k] = a[i];
				a[i] = temp;
				permute(result, a, k + 1);
				temp = a[k];
				a[k] = a[i];
				a[i] = temp;
			}
		}
	}

	public static Integer[] toObject(int[] intArray) {

		Integer[] result = new Integer[intArray.length];
		for (int i = 0; i < intArray.length; i++) {
			result[i] = Integer.valueOf(intArray[i]);
		}
		return result;
	}

}

package com.happyzleaf.voteparty;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

public class Utils {
	private static Random random = new Random();

	@Nullable
	public static <T> T getRandomElement(Collection<T> collection) {
		if (collection.size() == 0) {
			return null;
		}

		return (T) collection.toArray()[random.nextInt(collection.size())];
	}
}

package ravensproject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

public class Agent {
	public Agent() {
	}

	public int Solve(RavensProblem problem) throws IOException {
		System.out.println("Ravens Problem: " + problem.getName());
		HashMap<String, RavensFigure> Ravefigures = problem.getFigures();
		int answer = -1;

		// Algo 1.
		List<RavensFigure> row1 = new ArrayList<>();
		row1.add(Ravefigures.get("A"));
		row1.add(Ravefigures.get("B"));
		row1.add(Ravefigures.get("C"));
		long row1_pix = blackPixelCount(row1, true);

		List<RavensFigure> row2 = new ArrayList<>();
		row2.add(Ravefigures.get("D"));
		row2.add(Ravefigures.get("E"));
		row2.add(Ravefigures.get("F"));
		long row2_pix = blackPixelCount(row2, true);

		List<RavensFigure> rowAns = new ArrayList<>();
		rowAns.add(Ravefigures.get("G"));
		rowAns.add(Ravefigures.get("H"));
		long rowAns_pix = blackPixelCount(rowAns, true);

		long ansFigure_pix = Math.abs(2 * row2_pix - rowAns_pix - row1_pix);
		long error = (long) (0.014 * ansFigure_pix);
		System.out.println("error is: " + error);
		if (problem.getName().contains("D")) {
			System.out.println("ansFigure: " + ansFigure_pix);
			for (int i = 1; i <= 8; i++) {
				long temp_pix = blackPixel(Ravefigures.get(String.valueOf(i)));
				System.out.println("Ans " + i + ": " + temp_pix);
				System.out.println("difference is: "
						+ Math.abs(temp_pix - ansFigure_pix));
				if (Math.abs(temp_pix - ansFigure_pix) <= error) {
					System.out.println("Answer chosen: " + i);
					return i;
				}
			}
			// Algo 2. A.
		} else if (problem.getName().contains("E")) {
			long c = blackPixel(Ravefigures.get("F"));

			long g = blackPixel(Ravefigures.get("G"));
			long h = blackPixel(Ravefigures.get("H"));

			int state = -1;
			long orAB = pixelCounter(Ravefigures.get("D"),
					Ravefigures.get("E"), 1);
			long xorAB = pixelCounter(Ravefigures.get("D"),
					Ravefigures.get("E"), 2);
			long andAB = pixelCounter(Ravefigures.get("D"),
					Ravefigures.get("E"), 0);

			if (Math.abs(c - orAB) <= 0.1 * c) {
				state = 1;
			} else if (Math.abs(c - xorAB) <= 0.1 * c) {
				state = 2;
			} else if (Math.abs(c - andAB) <= 0.1 * c) {
				state = 0;
			}
			System.out.println("OR: " + Math.abs(c - orAB));
			System.out.println("XOR: " + Math.abs(c - xorAB));
			System.out.println("AND: " + Math.abs(c - andAB));
			System.out.println("STATE: " + state);
			if (state > -1) {
				long logicOpGH = pixelCounter(Ravefigures.get("G"),
						Ravefigures.get("H"), state);
				System.out.println("Logical: " + logicOpGH);
				for (int i = 1; i <= 8; i++) {
					long ans = blackPixel(Ravefigures.get(String.valueOf(i)));
					System.out.println("ans " + i + ": " + ans);
					if (Math.abs(ans - logicOpGH) <= 0.1 * ans) {
						return i;
					}

				}
			}
		}

		// Algo 4. catch all
		long ans_pix = Math.abs(blackPixel(Ravefigures.get("G"))
				- blackPixel(Ravefigures.get("D"))
				+ blackPixel(Ravefigures.get("F")));
		long closest_pix = Long.MAX_VALUE;
		System.out.println("ans_pix: " + ans_pix);
		for (int i = 1; i <= 8; i++) {
			long diff = Math.abs(blackPixel(Ravefigures.get(String.valueOf(i)))
					- ans_pix);
			System.out.println("diff: " + diff);
			if (diff < closest_pix) {
				closest_pix = diff;
				answer = i;

			}
		}
		System.out.println("Answer chosen: " + answer);
		return answer;
	}

	private long blackPixel(RavensFigure thisFigure) throws IOException {
		BufferedImage figureImage = ImageIO.read(new File(thisFigure
				.getVisual()));
		long black = 0, total = figureImage.getWidth()
				* figureImage.getHeight();
		for (int i = 0; i < figureImage.getWidth(); i++) {
			for (int j = 0; j < figureImage.getHeight(); j++) {
				black += figureImage.getRGB(i, j) != -1 ? 1 : 0;
			}
		}
		return black;
	}

	private long blackPixelCount(List<RavensFigure> figures, boolean pos)
			throws IOException {
		long pixelCount = 0;
		for (RavensFigure figure : figures) {

			pixelCount += pos ? blackPixel(figure) : -1 * blackPixel(figure);
		}
		return pixelCount;
	}

	private long pixelCounter(RavensFigure figure1, RavensFigure figure2,
			int state) throws IOException {
		long pixCounter = 0;
		BufferedImage figure1Image = ImageIO
				.read(new File(figure1.getVisual()));
		BufferedImage figure2Image = ImageIO
				.read(new File(figure2.getVisual()));
		for (int i = 0; i < figure1Image.getWidth(); i++) {
			for (int j = 0; j < figure1Image.getHeight(); j++) {

				switch (state) {
				// AND
				case 0:
					pixCounter += figure1Image.getRGB(i, j) != -1
							&& figure2Image.getRGB(i, j) != -1 ? 1 : 0;
					break;
				// OR
				case 1:

					if (figure1Image.getRGB(i, j) != -1
					|| figure2Image.getRGB(i, j) != -1) {
						pixCounter++;
					}

					break;
				// XOR
				case 2:
					if (figure1Image.getRGB(i, j) != -1
							|| figure2Image.getRGB(i, j) != -1) {

						if (!(figure1Image.getRGB(i, j) != -1 && figure2Image
								.getRGB(i, j) != -1)) {
							pixCounter++;
						}
					}

					break;

				}

			}
		}

		return pixCounter;
	}

}

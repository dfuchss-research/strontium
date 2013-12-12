package srl.recognition.paleo.multistroke;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import srl.core.sketch.Interpretation;
import srl.core.sketch.Point;
import srl.core.sketch.Shape;
import srl.core.sketch.Stroke;
import srl.core.sketch.comparators.TimePeriodComparator;
import srl.core.util.IsAConstants;
import srl.recognition.paleo.Fit;
import srl.recognition.paleo.LineFit;

public class TrussRecognizer {
	
	/**
	 * Input shapes
	 */
	private List<Shape> m_input;
	
	/**
	 * Output shapes
	 */
	private List<Shape> m_output;
	
	public static final String TRUSS = "Truss";

	/**
	 * Constructor
	 * 
	 * @param input
	 *            input shapes
	 */
	public TrussRecognizer(List<Shape> input) {
		m_input = input;
		Collections.sort(m_input, new TimePeriodComparator());
	}
	
	public List<Shape> recognize() {
		m_output = new ArrayList<Shape>();
		if (m_input.size() <= 0)
			return m_output;
		// contains strokes + substrokes (if stroke is poly)
		List<Shape> possibleTruss = new ArrayList<Shape>();

		// contains original strokes
		List<Shape> possibleTrussOrig = new ArrayList<Shape>();

		// populate m_output
		for (Shape shape : m_input) {
			// non-lines go into output
			if (!shape.getInterpretation().label.equals(Fit.LINE)
					&& !shape.getInterpretation().label.startsWith(Fit.POLYGON)
					&& !shape.getInterpretation().label
							.startsWith(Fit.POLYLINE)) {
				m_output.add(shape);
				generateTruss(possibleTrussOrig, possibleTruss);
				possibleTruss.clear();
				possibleTrussOrig.clear();
				continue;
			}
			// handle polyline/polygon
			if (shape.getInterpretation().label.startsWith(Fit.POLYGON)
					|| shape.getInterpretation().label.startsWith(Fit.POLYLINE)) {
				boolean continuationFailed = false;
				// handle individual lines
				for (Shape ss : shape.getShapes()) {
					if (continuationFailed)
						break;
					if (isTrussContinuation(possibleTruss, ss)) {
						possibleTruss.add(ss);
					} else {
						continuationFailed = true;
					}
				}

				boolean added = false;

				// generate truss
				if (continuationFailed) {
					if (possibleTrussOrig.size() <= 0) {
						possibleTrussOrig.add(shape);
						added = true;
					}
					// create truss shape
					generateTruss(possibleTrussOrig, possibleTruss);
					possibleTruss.clear();
					possibleTrussOrig.clear();
				}

				if (!added)
					possibleTrussOrig.add(shape);
			}

			// handle the line
			else {

				// truss continues
				if (isTrussContinuation(possibleTruss, shape)) {
					possibleTruss.add(shape);
					possibleTrussOrig.add(shape);
				}

				// we have a line but its not part of the current truss
				else {
					// create truss shape
					generateTruss(possibleTrussOrig, possibleTruss);
					possibleTruss.clear();
					possibleTrussOrig.clear();
					possibleTruss.add(shape);
					possibleTrussOrig.add(shape);
				}
			}
		}
		generateTruss(possibleTrussOrig, possibleTruss);
		Collections.sort(m_output, new TimePeriodComparator());
		return m_output;
	
}

	private boolean isTrussContinuation(List<Shape> current, Shape newShape) {
		// TODO add the new line here and see if it makes up a truss
		return true;
	}

	private void generateTruss(List<Shape> possibleTrussOrig,
			List<Shape> possibleTruss) {
		
		Truss newShape = new Truss();
		newShape.belongsToTruss(possibleTruss);
		if(newShape.evaluateTruss() == 0) {
			newShape.setLabel(TRUSS);
			newShape.setAttribute(IsAConstants.MID_LEVEL_SHAPE,
					IsAConstants.MID_LEVEL_SHAPE);
			// newShape.setRecognitionTime(System.currentTimeMillis());
			List<Stroke> subStrokes = new ArrayList<Stroke>();
			List<Shape> subShapes = new ArrayList<Shape>();
			double conf = newShape.getConfidence();
			for (Shape s : possibleTrussOrig) {
				subShapes.add(s);
				subStrokes.addAll(s.getStrokes());
				if (s.getInterpretation() != null)
					conf = Math.min(s.getInterpretation().confidence, conf);
			}
			newShape.setStrokes(subStrokes);
			newShape.setShapes(subShapes);
			newShape.getInterpretation().confidence = (conf);
			m_output.add(newShape);
		} else {
			m_output.addAll(possibleTrussOrig);
		}
	}
	
	public static void main(String args[]) {

		List<Shape> shapes = new ArrayList<Shape>();
		
		Stroke stroke0 = new Stroke();
		stroke0.addPoint(new Point(199.0, 309.0, 1384235419075L));
		stroke0.addPoint(new Point(199.0, 307.0, 1384235419211L));
		stroke0.addPoint(new Point(204.0, 303.0, 1384235419219L));
		stroke0.addPoint(new Point(207.0, 301.0, 1384235419227L));
		stroke0.addPoint(new Point(210.0, 297.0, 1384235419235L));
		stroke0.addPoint(new Point(214.0, 291.0, 1384235419243L));
		stroke0.addPoint(new Point(217.0, 289.0, 1384235419251L));
		stroke0.addPoint(new Point(220.0, 284.0, 1384235419259L));
		stroke0.addPoint(new Point(222.0, 275.0, 1384235419267L));
		stroke0.addPoint(new Point(224.0, 271.0, 1384235419275L));
		stroke0.addPoint(new Point(225.0, 267.0, 1384235419283L));
		stroke0.addPoint(new Point(226.0, 262.0, 1384235419291L));
		stroke0.addPoint(new Point(228.0, 259.0, 1384235419299L));
		stroke0.addPoint(new Point(228.0, 257.0, 1384235419307L));
		stroke0.addPoint(new Point(229.0, 254.0, 1384235419315L));
		stroke0.addPoint(new Point(231.0, 252.0, 1384235419323L));
		stroke0.addPoint(new Point(233.0, 248.0, 1384235419331L));
		stroke0.addPoint(new Point(234.0, 246.0, 1384235419339L));
		stroke0.addPoint(new Point(236.0, 244.0, 1384235419347L));
		stroke0.addPoint(new Point(239.0, 239.0, 1384235419355L));
		stroke0.addPoint(new Point(242.0, 236.0, 1384235419363L));
		stroke0.addPoint(new Point(244.0, 232.0, 1384235419371L));
		stroke0.addPoint(new Point(246.0, 230.0, 1384235419379L));
		stroke0.addPoint(new Point(248.0, 228.0, 1384235419387L));
		stroke0.addPoint(new Point(251.0, 225.0, 1384235419395L));
		stroke0.addPoint(new Point(253.0, 223.0, 1384235419411L));
		stroke0.addPoint(new Point(255.0, 221.0, 1384235419427L));
		stroke0.addPoint(new Point(256.0, 220.0, 1384235419443L));
		stroke0.addPoint(new Point(257.0, 219.0, 1384235419451L));
		stroke0.addPoint(new Point(258.0, 218.0, 1384235419459L));
		stroke0.addPoint(new Point(259.0, 217.0, 1384235419467L));
		stroke0.addPoint(new Point(260.0, 216.0, 1384235419483L));
		stroke0.addPoint(new Point(261.0, 215.0, 1384235419499L));
		stroke0.addPoint(new Point(261.0, 215.0, 1384235419651L));
		Shape shape0 = LineFit.getLineFit(stroke0);
		shapes.add(shape0);

		Stroke stroke1 = new Stroke();
		stroke1.addPoint(new Point(264.0, 214.0, 1384235419930L));
		stroke1.addPoint(new Point(265.0, 214.0, 1384235420003L));
		stroke1.addPoint(new Point(265.0, 216.0, 1384235420011L));
		stroke1.addPoint(new Point(266.0, 219.0, 1384235420019L));
		stroke1.addPoint(new Point(267.0, 222.0, 1384235420028L));
		stroke1.addPoint(new Point(267.0, 225.0, 1384235420035L));
		stroke1.addPoint(new Point(267.0, 228.0, 1384235420044L));
		stroke1.addPoint(new Point(270.0, 233.0, 1384235420051L));
		stroke1.addPoint(new Point(273.0, 239.0, 1384235420059L));
		stroke1.addPoint(new Point(276.0, 244.0, 1384235420067L));
		stroke1.addPoint(new Point(279.0, 250.0, 1384235420075L));
		stroke1.addPoint(new Point(281.0, 254.0, 1384235420083L));
		stroke1.addPoint(new Point(283.0, 259.0, 1384235420091L));
		stroke1.addPoint(new Point(285.0, 261.0, 1384235420099L));
		stroke1.addPoint(new Point(286.0, 265.0, 1384235420107L));
		stroke1.addPoint(new Point(288.0, 266.0, 1384235420115L));
		stroke1.addPoint(new Point(289.0, 269.0, 1384235420123L));
		stroke1.addPoint(new Point(289.0, 270.0, 1384235420131L));
		stroke1.addPoint(new Point(292.0, 273.0, 1384235420139L));
		stroke1.addPoint(new Point(293.0, 277.0, 1384235420147L));
		stroke1.addPoint(new Point(295.0, 278.0, 1384235420155L));
		stroke1.addPoint(new Point(295.0, 280.0, 1384235420163L));
		stroke1.addPoint(new Point(298.0, 282.0, 1384235420171L));
		stroke1.addPoint(new Point(299.0, 284.0, 1384235420179L));
		stroke1.addPoint(new Point(300.0, 286.0, 1384235420187L));
		stroke1.addPoint(new Point(301.0, 287.0, 1384235420195L));
		stroke1.addPoint(new Point(301.0, 288.0, 1384235420203L));
		stroke1.addPoint(new Point(302.0, 288.0, 1384235420211L));
		stroke1.addPoint(new Point(302.0, 289.0, 1384235420227L));
		stroke1.addPoint(new Point(302.0, 290.0, 1384235420243L));
		stroke1.addPoint(new Point(302.0, 291.0, 1384235420251L));
		stroke1.addPoint(new Point(302.0, 292.0, 1384235420291L));
		stroke1.addPoint(new Point(302.0, 293.0, 1384235420299L));
		stroke1.addPoint(new Point(303.0, 294.0, 1384235420307L));
		stroke1.addPoint(new Point(304.0, 298.0, 1384235420315L));
		stroke1.addPoint(new Point(304.0, 300.0, 1384235420323L));
		stroke1.addPoint(new Point(306.0, 303.0, 1384235420331L));
		stroke1.addPoint(new Point(308.0, 306.0, 1384235420339L));
		stroke1.addPoint(new Point(309.0, 311.0, 1384235420347L));
		stroke1.addPoint(new Point(309.0, 313.0, 1384235420355L));
		stroke1.addPoint(new Point(309.0, 314.0, 1384235420363L));
		stroke1.addPoint(new Point(311.0, 316.0, 1384235420371L));
		stroke1.addPoint(new Point(311.0, 317.0, 1384235420379L));
		stroke1.addPoint(new Point(311.0, 317.0, 1384235420451L));
		Shape shape1;
		shape1 = LineFit.getLineFit(stroke1);
		shapes.add(shape1);
		Stroke stroke2 = new Stroke();
		stroke2.addPoint(new Point(197.0, 311.0, 1384235421202L));
		stroke2.addPoint(new Point(201.0, 311.0, 1384235421275L));
		stroke2.addPoint(new Point(205.0, 311.0, 1384235421283L));
		stroke2.addPoint(new Point(210.0, 311.0, 1384235421291L));
		stroke2.addPoint(new Point(220.0, 311.0, 1384235421299L));
		stroke2.addPoint(new Point(227.0, 311.0, 1384235421307L));
		stroke2.addPoint(new Point(235.0, 311.0, 1384235421315L));
		stroke2.addPoint(new Point(242.0, 311.0, 1384235421323L));
		stroke2.addPoint(new Point(247.0, 311.0, 1384235421331L));
		stroke2.addPoint(new Point(251.0, 311.0, 1384235421339L));
		stroke2.addPoint(new Point(256.0, 311.0, 1384235421347L));
		stroke2.addPoint(new Point(257.0, 311.0, 1384235421355L));
		stroke2.addPoint(new Point(258.0, 311.0, 1384235421363L));
		stroke2.addPoint(new Point(259.0, 311.0, 1384235421379L));
		stroke2.addPoint(new Point(260.0, 311.0, 1384235421451L));
		stroke2.addPoint(new Point(262.0, 311.0, 1384235421459L));
		stroke2.addPoint(new Point(265.0, 311.0, 1384235421467L));
		stroke2.addPoint(new Point(267.0, 311.0, 1384235421475L));
		stroke2.addPoint(new Point(270.0, 311.0, 1384235421483L));
		stroke2.addPoint(new Point(273.0, 311.0, 1384235421491L));
		stroke2.addPoint(new Point(275.0, 311.0, 1384235421499L));
		stroke2.addPoint(new Point(278.0, 311.0, 1384235421507L));
		stroke2.addPoint(new Point(280.0, 311.0, 1384235421515L));
		stroke2.addPoint(new Point(282.0, 311.0, 1384235421523L));
		stroke2.addPoint(new Point(286.0, 311.0, 1384235421531L));
		stroke2.addPoint(new Point(288.0, 311.0, 1384235421539L));
		stroke2.addPoint(new Point(290.0, 311.0, 1384235421555L));
		stroke2.addPoint(new Point(291.0, 311.0, 1384235421571L));
		stroke2.addPoint(new Point(291.0, 312.0, 1384235421595L));
		stroke2.addPoint(new Point(292.0, 312.0, 1384235421603L));
		stroke2.addPoint(new Point(293.0, 313.0, 1384235421611L));
		stroke2.addPoint(new Point(294.0, 313.0, 1384235421619L));
		stroke2.addPoint(new Point(295.0, 313.0, 1384235421627L));
		stroke2.addPoint(new Point(296.0, 314.0, 1384235421635L));
		stroke2.addPoint(new Point(299.0, 314.0, 1384235421643L));
		stroke2.addPoint(new Point(301.0, 315.0, 1384235421651L));
		stroke2.addPoint(new Point(303.0, 315.0, 1384235421667L));
		stroke2.addPoint(new Point(304.0, 316.0, 1384235421675L));
		stroke2.addPoint(new Point(305.0, 316.0, 1384235421683L));
		stroke2.addPoint(new Point(306.0, 316.0, 1384235421691L));
		stroke2.addPoint(new Point(307.0, 317.0, 1384235421699L));
		stroke2.addPoint(new Point(308.0, 317.0, 1384235421723L));
		stroke2.addPoint(new Point(309.0, 317.0, 1384235421739L));
		stroke2.addPoint(new Point(309.0, 317.0, 1384235421915L));

		Shape shape2 = LineFit.getLineFit(stroke2);
		shapes.add(shape2);

		Stroke stroke3 = new Stroke();
		stroke3.addPoint(new Point(264.0, 213.0, 1384235423034L));
		stroke3.addPoint(new Point(265.0, 213.0, 1384235423091L));
		stroke3.addPoint(new Point(266.0, 213.0, 1384235423099L));
		stroke3.addPoint(new Point(268.0, 213.0, 1384235423107L));
		stroke3.addPoint(new Point(269.0, 213.0, 1384235423115L));
		stroke3.addPoint(new Point(273.0, 213.0, 1384235423123L));
		stroke3.addPoint(new Point(276.0, 213.0, 1384235423131L));
		stroke3.addPoint(new Point(279.0, 213.0, 1384235423139L));
		stroke3.addPoint(new Point(286.0, 213.0, 1384235423147L));
		stroke3.addPoint(new Point(289.0, 213.0, 1384235423155L));
		stroke3.addPoint(new Point(294.0, 213.0, 1384235423163L));
		stroke3.addPoint(new Point(300.0, 213.0, 1384235423171L));
		stroke3.addPoint(new Point(304.0, 214.0, 1384235423179L));
		stroke3.addPoint(new Point(308.0, 215.0, 1384235423187L));
		stroke3.addPoint(new Point(312.0, 215.0, 1384235423195L));
		stroke3.addPoint(new Point(314.0, 215.0, 1384235423203L));
		stroke3.addPoint(new Point(315.0, 215.0, 1384235423211L));
		stroke3.addPoint(new Point(316.0, 215.0, 1384235423219L));
		stroke3.addPoint(new Point(317.0, 215.0, 1384235423227L));
		stroke3.addPoint(new Point(318.0, 215.0, 1384235423235L));
		stroke3.addPoint(new Point(319.0, 215.0, 1384235423275L));
		stroke3.addPoint(new Point(320.0, 215.0, 1384235423291L));
		stroke3.addPoint(new Point(321.0, 215.0, 1384235423299L));
		stroke3.addPoint(new Point(322.0, 215.0, 1384235423307L));
		stroke3.addPoint(new Point(324.0, 215.0, 1384235423323L));
		stroke3.addPoint(new Point(324.0, 216.0, 1384235423331L));
		stroke3.addPoint(new Point(324.0, 217.0, 1384235423339L));
		stroke3.addPoint(new Point(325.0, 217.0, 1384235423347L));
		stroke3.addPoint(new Point(326.0, 217.0, 1384235423355L));
		stroke3.addPoint(new Point(327.0, 217.0, 1384235423363L));
		stroke3.addPoint(new Point(329.0, 217.0, 1384235423371L));
		stroke3.addPoint(new Point(329.0, 218.0, 1384235423379L));
		stroke3.addPoint(new Point(330.0, 219.0, 1384235423387L));
		stroke3.addPoint(new Point(331.0, 219.0, 1384235423403L));
		stroke3.addPoint(new Point(333.0, 219.0, 1384235423411L));
		stroke3.addPoint(new Point(335.0, 219.0, 1384235423419L));
		stroke3.addPoint(new Point(336.0, 219.0, 1384235423427L));
		stroke3.addPoint(new Point(337.0, 219.0, 1384235423435L));
		stroke3.addPoint(new Point(338.0, 219.0, 1384235423443L));
		stroke3.addPoint(new Point(339.0, 219.0, 1384235423451L));
		stroke3.addPoint(new Point(341.0, 219.0, 1384235423467L));
		stroke3.addPoint(new Point(344.0, 219.0, 1384235423475L));
		stroke3.addPoint(new Point(346.0, 219.0, 1384235423483L));
		stroke3.addPoint(new Point(349.0, 219.0, 1384235423491L));
		stroke3.addPoint(new Point(351.0, 219.0, 1384235423499L));
		stroke3.addPoint(new Point(354.0, 219.0, 1384235423507L));
		stroke3.addPoint(new Point(356.0, 219.0, 1384235423515L));
		stroke3.addPoint(new Point(357.0, 220.0, 1384235423523L));
		stroke3.addPoint(new Point(358.0, 220.0, 1384235423531L));
		stroke3.addPoint(new Point(358.0, 221.0, 1384235423539L));
		stroke3.addPoint(new Point(361.0, 222.0, 1384235423547L));
		stroke3.addPoint(new Point(362.0, 222.0, 1384235423563L));
		stroke3.addPoint(new Point(363.0, 222.0, 1384235423571L));
		stroke3.addPoint(new Point(364.0, 222.0, 1384235423579L));
		stroke3.addPoint(new Point(365.0, 222.0, 1384235423587L));
		stroke3.addPoint(new Point(366.0, 222.0, 1384235423595L));
		stroke3.addPoint(new Point(367.0, 222.0, 1384235423603L));
		stroke3.addPoint(new Point(368.0, 222.0, 1384235423611L));
		stroke3.addPoint(new Point(369.0, 222.0, 1384235423619L));
		stroke3.addPoint(new Point(371.0, 222.0, 1384235423627L));
		stroke3.addPoint(new Point(372.0, 222.0, 1384235423635L));
		stroke3.addPoint(new Point(373.0, 222.0, 1384235423643L));
		stroke3.addPoint(new Point(374.0, 222.0, 1384235423659L));
		stroke3.addPoint(new Point(375.0, 222.0, 1384235423675L));
		stroke3.addPoint(new Point(376.0, 222.0, 1384235423707L));
		stroke3.addPoint(new Point(376.0, 222.0, 1384235423715L));
		Shape shape3 = LineFit.getLineFit(stroke3);  
		shapes.add(shape3);
		Stroke stroke4 = new Stroke();
		stroke4.addPoint(new Point(376.0, 221.0, 1384235424578L));
		stroke4.addPoint(new Point(375.0, 222.0, 1384235424659L));
		stroke4.addPoint(new Point(374.0, 224.0, 1384235424667L));
		stroke4.addPoint(new Point(374.0, 227.0, 1384235424675L));
		stroke4.addPoint(new Point(374.0, 228.0, 1384235424683L));
		stroke4.addPoint(new Point(372.0, 230.0, 1384235424691L));
		stroke4.addPoint(new Point(371.0, 233.0, 1384235424699L));
		stroke4.addPoint(new Point(370.0, 233.0, 1384235424707L));
		stroke4.addPoint(new Point(369.0, 235.0, 1384235424715L));
		stroke4.addPoint(new Point(367.0, 237.0, 1384235424723L));
		stroke4.addPoint(new Point(366.0, 238.0, 1384235424731L));
		stroke4.addPoint(new Point(363.0, 241.0, 1384235424739L));
		stroke4.addPoint(new Point(361.0, 243.0, 1384235424755L));
		stroke4.addPoint(new Point(359.0, 245.0, 1384235424763L));
		stroke4.addPoint(new Point(357.0, 247.0, 1384235424771L));
		stroke4.addPoint(new Point(354.0, 248.0, 1384235424779L));
		stroke4.addPoint(new Point(353.0, 249.0, 1384235424787L));
		stroke4.addPoint(new Point(351.0, 251.0, 1384235424795L));
		stroke4.addPoint(new Point(349.0, 252.0, 1384235424803L));
		stroke4.addPoint(new Point(348.0, 254.0, 1384235424811L));
		stroke4.addPoint(new Point(346.0, 255.0, 1384235424819L));
		stroke4.addPoint(new Point(345.0, 257.0, 1384235424827L));
		stroke4.addPoint(new Point(344.0, 258.0, 1384235424835L));
		stroke4.addPoint(new Point(343.0, 260.0, 1384235424843L));
		stroke4.addPoint(new Point(341.0, 262.0, 1384235424851L));
		stroke4.addPoint(new Point(340.0, 263.0, 1384235424859L));
		stroke4.addPoint(new Point(339.0, 265.0, 1384235424867L));
		stroke4.addPoint(new Point(338.0, 265.0, 1384235424875L));
		stroke4.addPoint(new Point(338.0, 267.0, 1384235424883L));
		stroke4.addPoint(new Point(337.0, 268.0, 1384235424891L));
		stroke4.addPoint(new Point(336.0, 269.0, 1384235424899L));
		stroke4.addPoint(new Point(335.0, 270.0, 1384235424907L));
		stroke4.addPoint(new Point(335.0, 271.0, 1384235424915L));
		stroke4.addPoint(new Point(335.0, 272.0, 1384235424923L));
		stroke4.addPoint(new Point(332.0, 274.0, 1384235424931L));
		stroke4.addPoint(new Point(331.0, 275.0, 1384235424939L));
		stroke4.addPoint(new Point(329.0, 278.0, 1384235424947L));
		stroke4.addPoint(new Point(326.0, 280.0, 1384235424955L));
		stroke4.addPoint(new Point(325.0, 282.0, 1384235424963L));
		stroke4.addPoint(new Point(322.0, 284.0, 1384235424971L));
		stroke4.addPoint(new Point(321.0, 287.0, 1384235424979L));
		stroke4.addPoint(new Point(318.0, 289.0, 1384235424987L));
		stroke4.addPoint(new Point(318.0, 292.0, 1384235424995L));
		stroke4.addPoint(new Point(318.0, 293.0, 1384235425003L));
		stroke4.addPoint(new Point(317.0, 296.0, 1384235425011L));
		stroke4.addPoint(new Point(317.0, 297.0, 1384235425019L));
		stroke4.addPoint(new Point(317.0, 298.0, 1384235425027L));
		stroke4.addPoint(new Point(316.0, 298.0, 1384235425035L));
		stroke4.addPoint(new Point(316.0, 300.0, 1384235425043L));
		stroke4.addPoint(new Point(316.0, 301.0, 1384235425059L));
		stroke4.addPoint(new Point(315.0, 303.0, 1384235425067L));
		stroke4.addPoint(new Point(314.0, 303.0, 1384235425075L));
		stroke4.addPoint(new Point(314.0, 305.0, 1384235425083L));
		stroke4.addPoint(new Point(314.0, 306.0, 1384235425099L));
		stroke4.addPoint(new Point(313.0, 307.0, 1384235425107L));
		stroke4.addPoint(new Point(313.0, 308.0, 1384235425115L));
		stroke4.addPoint(new Point(312.0, 309.0, 1384235425131L));
		stroke4.addPoint(new Point(311.0, 310.0, 1384235425155L));
		stroke4.addPoint(new Point(310.0, 311.0, 1384235425163L));
		stroke4.addPoint(new Point(309.0, 311.0, 1384235425171L));
		stroke4.addPoint(new Point(308.0, 313.0, 1384235425179L));
		stroke4.addPoint(new Point(307.0, 314.0, 1384235425187L));
		stroke4.addPoint(new Point(306.0, 315.0, 1384235425195L));
		stroke4.addPoint(new Point(305.0, 317.0, 1384235425203L));
		stroke4.addPoint(new Point(304.0, 319.0, 1384235425219L));
		stroke4.addPoint(new Point(303.0, 320.0, 1384235425235L));
		stroke4.addPoint(new Point(303.0, 321.0, 1384235425243L));
		stroke4.addPoint(new Point(303.0, 322.0, 1384235425259L));
		stroke4.addPoint(new Point(302.0, 322.0, 1384235425275L));
		stroke4.addPoint(new Point(302.0, 323.0, 1384235425307L));
		stroke4.addPoint(new Point(301.0, 323.0, 1384235425395L));
		stroke4.addPoint(new Point(301.0, 321.0, 1384235425443L));
		stroke4.addPoint(new Point(301.0, 321.0, 1384235425443L));
		Shape shape4 = LineFit.getLineFit(stroke4);
		shapes.add(shape4);

		Stroke stroke5 = new Stroke();
		stroke5.addPoint(new Point(375.0, 218.0, 1384235426162L));
		stroke5.addPoint(new Point(375.0, 219.0, 1384235426227L));
		stroke5.addPoint(new Point(376.0, 220.0, 1384235426243L));
		stroke5.addPoint(new Point(376.0, 222.0, 1384235426251L));
		stroke5.addPoint(new Point(377.0, 223.0, 1384235426259L));
		stroke5.addPoint(new Point(378.0, 225.0, 1384235426267L));
		stroke5.addPoint(new Point(380.0, 228.0, 1384235426283L));
		stroke5.addPoint(new Point(383.0, 232.0, 1384235426291L));
		stroke5.addPoint(new Point(387.0, 235.0, 1384235426299L));
		stroke5.addPoint(new Point(391.0, 239.0, 1384235426307L));
		stroke5.addPoint(new Point(393.0, 241.0, 1384235426315L));
		stroke5.addPoint(new Point(397.0, 246.0, 1384235426323L));
		stroke5.addPoint(new Point(400.0, 249.0, 1384235426331L));
		stroke5.addPoint(new Point(402.0, 251.0, 1384235426339L));
		stroke5.addPoint(new Point(404.0, 253.0, 1384235426347L));
		stroke5.addPoint(new Point(407.0, 256.0, 1384235426355L));
		stroke5.addPoint(new Point(408.0, 257.0, 1384235426363L));
		stroke5.addPoint(new Point(410.0, 260.0, 1384235426371L));
		stroke5.addPoint(new Point(411.0, 260.0, 1384235426379L));
		stroke5.addPoint(new Point(412.0, 262.0, 1384235426387L));
		stroke5.addPoint(new Point(413.0, 263.0, 1384235426395L));
		stroke5.addPoint(new Point(414.0, 266.0, 1384235426403L));
		stroke5.addPoint(new Point(415.0, 267.0, 1384235426411L));
		stroke5.addPoint(new Point(416.0, 270.0, 1384235426419L));
		stroke5.addPoint(new Point(418.0, 274.0, 1384235426427L));
		stroke5.addPoint(new Point(420.0, 276.0, 1384235426435L));
		stroke5.addPoint(new Point(421.0, 277.0, 1384235426443L));
		stroke5.addPoint(new Point(422.0, 280.0, 1384235426451L));
		stroke5.addPoint(new Point(424.0, 281.0, 1384235426459L));
		stroke5.addPoint(new Point(425.0, 283.0, 1384235426467L));
		stroke5.addPoint(new Point(426.0, 285.0, 1384235426475L));
		stroke5.addPoint(new Point(428.0, 288.0, 1384235426483L));
		stroke5.addPoint(new Point(428.0, 289.0, 1384235426491L));
		stroke5.addPoint(new Point(429.0, 292.0, 1384235426499L));
		stroke5.addPoint(new Point(429.0, 294.0, 1384235426507L));
		stroke5.addPoint(new Point(430.0, 294.0, 1384235426515L));
		stroke5.addPoint(new Point(430.0, 296.0, 1384235426523L));
		stroke5.addPoint(new Point(430.0, 297.0, 1384235426531L));
		stroke5.addPoint(new Point(430.0, 298.0, 1384235426555L));
		stroke5.addPoint(new Point(430.0, 300.0, 1384235426579L));
		stroke5.addPoint(new Point(430.0, 301.0, 1384235426595L));
		stroke5.addPoint(new Point(430.0, 302.0, 1384235426603L));
		stroke5.addPoint(new Point(430.0, 303.0, 1384235426619L));
		stroke5.addPoint(new Point(431.0, 303.0, 1384235426643L));
		stroke5.addPoint(new Point(432.0, 303.0, 1384235426651L));
		stroke5.addPoint(new Point(432.0, 304.0, 1384235426659L));
		stroke5.addPoint(new Point(432.0, 305.0, 1384235426675L));
		stroke5.addPoint(new Point(432.0, 306.0, 1384235426691L));
		stroke5.addPoint(new Point(433.0, 307.0, 1384235426715L));
		stroke5.addPoint(new Point(433.0, 308.0, 1384235426723L));
		stroke5.addPoint(new Point(434.0, 308.0, 1384235426731L));
		stroke5.addPoint(new Point(435.0, 310.0, 1384235426739L));
		stroke5.addPoint(new Point(435.0, 311.0, 1384235426811L));
		stroke5.addPoint(new Point(435.0, 311.0, 1384235426851L));
		Shape shape5 = LineFit.getLineFit(stroke5);
		shapes.add(shape5);

		Stroke stroke6 = new Stroke();
		stroke6.addPoint(new Point(306.0, 314.0, 1384235427938L));
		stroke6.addPoint(new Point(307.0, 314.0, 1384235427963L));
		stroke6.addPoint(new Point(308.0, 314.0, 1384235427995L));
		stroke6.addPoint(new Point(312.0, 314.0, 1384235428003L));
		stroke6.addPoint(new Point(316.0, 314.0, 1384235428011L));
		stroke6.addPoint(new Point(322.0, 314.0, 1384235428019L));
		stroke6.addPoint(new Point(329.0, 314.0, 1384235428027L));
		stroke6.addPoint(new Point(335.0, 314.0, 1384235428035L));
		stroke6.addPoint(new Point(340.0, 314.0, 1384235428043L));
		stroke6.addPoint(new Point(349.0, 314.0, 1384235428051L));
		stroke6.addPoint(new Point(354.0, 314.0, 1384235428059L));
		stroke6.addPoint(new Point(360.0, 314.0, 1384235428067L));
		stroke6.addPoint(new Point(364.0, 314.0, 1384235428075L));
		stroke6.addPoint(new Point(367.0, 314.0, 1384235428083L));
		stroke6.addPoint(new Point(370.0, 314.0, 1384235428091L));
		stroke6.addPoint(new Point(372.0, 314.0, 1384235428099L));
		stroke6.addPoint(new Point(374.0, 314.0, 1384235428107L));
		stroke6.addPoint(new Point(375.0, 314.0, 1384235428123L));
		stroke6.addPoint(new Point(378.0, 314.0, 1384235428131L));
		stroke6.addPoint(new Point(379.0, 314.0, 1384235428139L));
		stroke6.addPoint(new Point(380.0, 314.0, 1384235428147L));
		stroke6.addPoint(new Point(382.0, 314.0, 1384235428155L));
		stroke6.addPoint(new Point(385.0, 314.0, 1384235428163L));
		stroke6.addPoint(new Point(388.0, 314.0, 1384235428171L));
		stroke6.addPoint(new Point(391.0, 314.0, 1384235428179L));
		stroke6.addPoint(new Point(393.0, 314.0, 1384235428187L));
		stroke6.addPoint(new Point(396.0, 314.0, 1384235428195L));
		stroke6.addPoint(new Point(399.0, 314.0, 1384235428203L));
		stroke6.addPoint(new Point(402.0, 314.0, 1384235428211L));
		stroke6.addPoint(new Point(405.0, 314.0, 1384235428219L));
		stroke6.addPoint(new Point(407.0, 314.0, 1384235428227L));
		stroke6.addPoint(new Point(410.0, 314.0, 1384235428235L));
		stroke6.addPoint(new Point(411.0, 314.0, 1384235428243L));
		stroke6.addPoint(new Point(412.0, 314.0, 1384235428251L));
		stroke6.addPoint(new Point(413.0, 314.0, 1384235428259L));
		stroke6.addPoint(new Point(414.0, 314.0, 1384235428267L));
		stroke6.addPoint(new Point(415.0, 314.0, 1384235428275L));
		stroke6.addPoint(new Point(416.0, 314.0, 1384235428307L));
		stroke6.addPoint(new Point(418.0, 314.0, 1384235428339L));
		stroke6.addPoint(new Point(419.0, 314.0, 1384235428347L));
		stroke6.addPoint(new Point(420.0, 314.0, 1384235428363L));
		stroke6.addPoint(new Point(421.0, 314.0, 1384235428379L));
		stroke6.addPoint(new Point(422.0, 314.0, 1384235428403L));
		stroke6.addPoint(new Point(423.0, 314.0, 1384235428427L));
		stroke6.addPoint(new Point(424.0, 314.0, 1384235428435L));
		stroke6.addPoint(new Point(425.0, 314.0, 1384235428451L));
		stroke6.addPoint(new Point(426.0, 314.0, 1384235428459L));
		stroke6.addPoint(new Point(427.0, 314.0, 1384235428467L));
		stroke6.addPoint(new Point(429.0, 314.0, 1384235428475L));
		stroke6.addPoint(new Point(430.0, 314.0, 1384235428491L));
		stroke6.addPoint(new Point(431.0, 314.0, 1384235428499L));
		stroke6.addPoint(new Point(432.0, 314.0, 1384235428507L));
		stroke6.addPoint(new Point(433.0, 314.0, 1384235428603L));
		stroke6.addPoint(new Point(434.0, 314.0, 1384235428627L));
		stroke6.addPoint(new Point(435.0, 314.0, 1384235428635L));
		stroke6.addPoint(new Point(436.0, 314.0, 1384235428683L));
		stroke6.addPoint(new Point(437.0, 314.0, 1384235428699L));
		stroke6.addPoint(new Point(437.0, 314.0, 1384235428858L));
		Shape shape6 = LineFit.getLineFit(stroke6);
		shapes.add(shape6);
		
		TrussRecognizer tr = new TrussRecognizer(shapes);
		List<Shape> results = tr.recognize();
		if(results.get(0).getInterpretation().label.equalsIgnoreCase(TRUSS))
			System.out.println("Truss detected with confidence " 
						+ results.get(0).getInterpretation().confidence);
	}
}
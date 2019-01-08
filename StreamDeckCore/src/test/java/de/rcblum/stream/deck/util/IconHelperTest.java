package de.rcblum.stream.deck.util;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.*;

import de.rcblum.stream.deck.items.animation.AnimationStack;

class IconHelperTest {

	@Test
	void testGetTextBoxAlphaValue() {
		IconHelper.setTextBoxAlphaValue(200);
		assertEquals(IconHelper.getTextBoxAlphaValue(), 200);
	}

	@Test
	void testSetTextBoxAlphaValue() {
		IconHelper.setTextBoxAlphaValue(200);
		assertEquals(IconHelper.getTextBoxAlphaValue(), 200);
	}

	@Test
	void testGetRollingTextPadding() {
		IconHelper.setRollingTextPadding(8);
		assertEquals(IconHelper.getRollingTextPadding(), 8);
	}

	@Test
	void testSetRollingTextPadding() {
		IconHelper.setRollingTextPadding(8);
		assertEquals(IconHelper.getRollingTextPadding(), 8);
	}

	@Test
	void testCreateFolderImage() {
		assertTrue(IconHelper.createFolderImage(Color.GREEN, true) instanceof SDImage);
	}

	@Test
	void testCreateColoredFrame() {
		assertTrue(IconHelper.createColoredFrame(Color.GREEN) instanceof SDImage);
	}

	@Test
	void testApplyAlpha() {
		IconHelper.applyAlpha(IconHelper.createColoredFrame(Color.GREEN).image, IconHelper.FRAME);
		assertTrue(true);
	}

	@Test
	void testAddTextSDImageStringInt() {
		assertTrue(IconHelper.addText(IconHelper.createFolderImage(Color.GREEN, true), "Test", IconHelper.TEXT_BOTTOM) instanceof SDImage);
	}

	@Test
	void testAddTextBufferedImageStringInt() {
		assertTrue(IconHelper.addText(IconHelper.createFolderImage(Color.GREEN, true).image, "Test", IconHelper.TEXT_BOTTOM) instanceof SDImage);
	}

	@Test
	void testAddTextSDImageStringIntFloat() {
		assertTrue(IconHelper.addText(IconHelper.createFolderImage(Color.GREEN, true), "Test", IconHelper.TEXT_BOTTOM, 20.0f) instanceof SDImage);
	}

	@Test
	void testAddTextBufferedImageStringIntFloat() {
		assertTrue(IconHelper.addText(IconHelper.createFolderImage(Color.GREEN, true).image, "Test", IconHelper.TEXT_BOTTOM, 20.0f) instanceof SDImage);
	}

	@Test
	void testCreateRollingTextAnimationSDImageStringInt() {
		assertTrue(IconHelper.createRollingTextAnimation(IconHelper.createFolderImage(Color.GREEN, true),
				"Long Test Text to create animations", IconHelper.TEXT_BOTTOM) instanceof AnimationStack);
	}

	@Test
	void testCreateRollingTextAnimationSDImageStringIntFloat() {
		assertTrue(IconHelper.createRollingTextAnimation(IconHelper.createFolderImage(Color.GREEN, true),
				"Long Test Text to create animations", IconHelper.TEXT_BOTTOM, 20.0f) instanceof AnimationStack);
	}

	@Test
	void testCacheImage() {
		assertTrue(IconHelper.cacheImage("TEST-PATH", IconHelper.createFolderImage(Color.GREEN, true).image) instanceof SDImage);
	}

	@Test
	void testConvertImageBufferedImage() {
		assertTrue(IconHelper.convertImage(IconHelper.createFolderImage(Color.GREEN, true).image) instanceof SDImage);
	}

	@Test
	void testConvertImageBufferedImageBoolean() {
		assertTrue(IconHelper.convertImage(IconHelper.createFolderImage(Color.GREEN, true).image, true) instanceof SDImage);
	}

	@Test
	void testApplyImage() {
		SDImage b1 = IconHelper.createFolderImage(Color.GREEN, true);
		BufferedImage b2 = IconHelper.createFolderImage(Color.GREEN, true).image;
		assertTrue(IconHelper.applyImage(b1, b2) instanceof SDImage);
	}

	@Test
	void testApplyFrame() {
		assertTrue(IconHelper.applyFrame(IconHelper.createColoredFrame(Color.GREEN).image) instanceof BufferedImage);
	}

	@Test
	void testCreateIconPackageStringStringStringAnimationStack() {
		//fail("Not yet implemented");
	}

	@Test
	void testCreateIconPackageStringStringStringArrayAnimationStack() {
		//fail("Not yet implemented");
	}

	@Test
	void testCreateResizedCopy() {
		assertTrue(IconHelper.createResizedCopy(IconHelper.createColoredFrame(Color.GREEN).image, false) instanceof BufferedImage);
	}

	@Test
	void testFillBackground() {
		assertTrue(IconHelper.fillBackground(IconHelper.createColoredFrame(Color.GREEN).image, Color.GREEN) instanceof BufferedImage);
	}

	@Test
	void testFlipHoriz() {
		assertTrue(IconHelper.flipHoriz(IconHelper.createColoredFrame(Color.GREEN).image) instanceof BufferedImage);
	}

	@Test
	void testGetImage() {
		assertTrue(IconHelper.getImage(IconHelper.TEMP_BLACK_ICON) instanceof SDImage);
	}

	@Test
	void testGetImageFromResource() {
		assertTrue(IconHelper.getImageFromResource("/resources/icons/frame.png") instanceof BufferedImage);
	}

	@Test
	void testLoadIconPackage() {
		//fail("Not yet implemented");
	}

	@Test
	void testLoadImageSafeString() {
		assertTrue(IconHelper.loadImageSafe("/resources/icons/frame.png") instanceof SDImage);
	}

	@Test
	void testLoadImageSafePath() {
		//fail("Not yet implemented");
	}

	@Test
	void testLoadImagePath() {
		//fail("Not yet implemented");
	}

	@Test
	void testLoadImageString() {
		//fail("Not yet implemented");
	}

	@Test
	void testLoadImageStringInputStreamBoolean() {
		//fail("Not yet implemented");
	}

	@Test
	void testLoadRawImagePath() {
		//fail("Not yet implemented");
	}

	@Test
	void testLoadRawImageStringInputStream() {
		//fail("Not yet implemented");
	}

	@Test
	void testLoadImageFromResource() {
		//fail("Not yet implemented");
	}

	@Test
	void testLoadImageFromResourceSafe() {
		//fail("Not yet implemented");
	}

	@Test
	void testLoadImagesFromGif() {
		//fail("Not yet implemented");
	}

	@Test
	void testRotate180() {
		//fail("Not yet implemented");
	}

}

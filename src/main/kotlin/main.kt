import actions.*
import image.Image
import java.awt.Point
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val image = ImageIO.read(File("src/main/resources/test.jpg"))
    val saturated = image.copy()
    val desaturated = image.copy()
    val sine = image.copy()
    val logistic = image.copy()
    val tangenth = image.copy()
    val arcustan = image.copy()
    val gd = image.copy()
    val smoothstep = image.copy()
    val grayscale = image.copy()
    val smooth = image.copy()

    Image(saturated).onEachPixel(saturate(0.2))
    Image(desaturated).onEachPixel(desaturate(1.0))
    Image(sine).onEachPixel(curves(sine()))
    Image(logistic).onEachPixel(curves(logistic()))
    Image(tangenth).onEachPixel(curves(tangenth()))
    Image(arcustan).onEachPixel(curves(arcustan()))
    Image(gd).onEachPixel(curves(gd()))
    Image(smoothstep).onEachPixel(curves(smoothstep()))
    Image(grayscale).onEachPixel(grayscale(3.0, 0.5, -0.5))
    Image(smooth).onEachPixel(smooth(0.2))

    ImageIO.write(saturated, "PNG", File("src/main/resources/0_saturated.png"))
    ImageIO.write(desaturated, "PNG", File("src/main/resources/1_desaturated.png"))
    ImageIO.write(sine, "PNG", File("src/main/resources/2_sine.png"))
    ImageIO.write(logistic, "PNG", File("src/main/resources/3_logistic.png"))
    ImageIO.write(tangenth, "PNG", File("src/main/resources/4_tangenth.png"))
    ImageIO.write(arcustan, "PNG", File("src/main/resources/5_arcustan.png"))
    ImageIO.write(gd, "PNG", File("src/main/resources/6_gd.png"))
    ImageIO.write(smoothstep, "PNG", File("src/main/resources/7_smoothstep.png"))
    ImageIO.write(grayscale, "PNG", File("src/main/resources/8_grayscale.png"))
    ImageIO.write(smooth, "PNG", File("src/main/resources/9_smooth.png"))
}

fun BufferedImage.copy(): BufferedImage {
    return BufferedImage(
        colorModel,
        copyData(raster.createCompatibleWritableRaster()),
        colorModel.isAlphaPremultiplied,
        null
    )
}

fun create(img: Image): BufferedImage {
    return BufferedImage(img.width, img.height, BufferedImage.TYPE_3BYTE_BGR).apply {
        data = Raster.createRaster(
            sampleModel,
            DataBufferByte(img.pixelData(), img.pixelData().size),
            Point()
        )
    }
}
package com.complexible.common.nio.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

/**
 * <p>Utility class for working with {@link Path}</p>
 *
 * @author  Michael Grove
 * @since   5.0
 * @version 5.0
 */
public final class Paths2 {

	private Paths2() {
		throw new AssertionError();
	}

	public static String toString(final Path thePath, final Charset theCharset) throws IOException {
		return new String(Files.readAllBytes(thePath), theCharset);
	}

	/**
	 * Returns a {@link Path} object for the given context using a new {@link FileSystem jar-based FileSystem}.
	 * The new {@link FileSystem} probably needs to be closed after using the {@link Path} object.
	 */
	public static Path withJarFileSystem(final String thePath, final Class<?> theClass) {
		try {
			String aUri = Resources.getResource(theClass, thePath).toURI().toString();
			final String[] aSplit = aUri.split("!");
			FileSystem aFs = FileSystems.newFileSystem(URI.create(aSplit[0]), Maps.newHashMap());
			return aFs.getPath(aSplit[1]);
		}
		catch (IOException | URISyntaxException e1) {
			throw new RuntimeException(e1);
		}
	}

	public static Path classPath(final String thePath) {
		try {
			return Paths.get(Paths2.class.getResource(thePath).toURI());
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static ByteSource asByteSource(final Path thePath) {
		return new ByteSource() {
			@Override
			public InputStream openStream() throws IOException {
				// will this work for non-standard FileSystem implementations such as s3?
				return Files.newInputStream(thePath);
			}
		};
	}

	public static void deleteOnExit(final Path thePath) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					delete(thePath);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void delete(final Path thePath) throws IOException {
		if (Files.isDirectory(thePath)) {
			delete(thePath, true /* delete root */);
		}
		else {
			Files.deleteIfExists(thePath);
		}
	}

	public static void deleteContents(final Path thePath) throws IOException {
		delete(thePath, false /* delete root */);
	}

	private static void delete(final Path thePath, final boolean theDeleteRoot) throws IOException {
		if (Files.exists(thePath)) {
			Files.walkFileTree(thePath, new SimpleFileVisitor<Path>() {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public FileVisitResult postVisitDirectory(final Path theDir, final IOException theException)
					throws IOException {

					if (!theDir.equals(thePath) || (theDir.equals(thePath) && theDeleteRoot)) {
						Files.deleteIfExists(theDir);
					}

					return super.postVisitDirectory(theDir, theException);
				}

				/**
				 * {@inheritDoc}
				 */
				@Override
				public FileVisitResult visitFile(final Path theFile, final BasicFileAttributes theAttrs)
					throws IOException {

					Files.deleteIfExists(theFile);

					return super.visitFile(theFile, theAttrs);
				}
			});
		}
	}

	public static void write(final String theInput, final Path theFile, final Charset theCharset) throws IOException {
		try (OutputStream aOut = Files.newOutputStream(theFile)) {
			aOut.write(theInput.getBytes(theCharset));
		}
	}

	public static Path createTempDir() {
		int TEMP_DIR_ATTEMPTS = 10;

		String baseName = System.currentTimeMillis() + "-";

		for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
			try {
				return Files.createTempDirectory(baseName + counter);
			}
			catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			catch (IllegalArgumentException e) {
				// i think this can be the case where the dir name is not unique.  so we'll try again
			}
		}
		throw new IllegalStateException("Failed to create directory within "
		                                + TEMP_DIR_ATTEMPTS + " attempts (tried "
		                                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
	}

	/**
	 * Convenience wrapper for {@link Files#list} which simply wraps the checked {@link IOException} as an
	 * {@link UncheckedIOException}.
	 *
	 * @param theFile   the file
	 * @return          the result of {@link Files#list}
	 *
	 * @throws UncheckedIOException    if {@link Files#list} throws an {@link IOException}
	 */
	public static Stream<Path> list(final Path theFile) {
		try {
			return Files.list(theFile);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Convenience wrapper for {@link Files#size}; returns the file size or {@code 0L} if the file does not exist
	 * like {@link File#length()}.
	 *
	 * @param theFile   the file
	 * @return          the result of {@link Files#size}, or {@code 0L} if the file does not exist
	 */
	public static long size(final Path theFile) {
		try {
			return Files.size(theFile);
		}
		catch (IOException e) {
			return 0;
		}
	}
}
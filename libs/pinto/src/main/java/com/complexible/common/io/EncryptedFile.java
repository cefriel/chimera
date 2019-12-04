/*
 * Copyright (c) 2005-2011 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.complexible.common.io;

import javax.crypto.CipherInputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;

import java.security.spec.AlgorithmParameterSpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.net.URI;

/**
 * <p>A extended File which will return an input or output stream for reading and writing to the file
 * in an encrypted/decrypted manner.  The same key must be used to read from an encrypted file as was used to write to it.</p>
 *
 * @author Michael Grove
 * @since 1.0
 */
public final class EncryptedFile extends File {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4733077945952710659L;

	/**
     * The Cipher to use for encryption
     */
    private transient Cipher mEncryptCipher;

    /**
     * The Cipher to use for decryption.
     */
    private transient Cipher mDecryptCipher;

    /**
     * Initialization vector for the algorithm spec
     */
    private static final byte[] IV = new byte[]{
            // Create an 8-byte initialization vector
            (byte)0x8F, 0x11, 0x40, (byte)0x9D,
            0x02, 0x62, 0x6F, 0x5B
    };

    /**
     * Create a new EncryptedFile
     * @param theFile the file to read/write to
     * @param theKey the key to use for encryption/decryption
     * @throws IllegalStateException thrown if there is an error creating a cipher.
     */
    public EncryptedFile(String theFile, String theKey) throws IllegalStateException {
        this(theFile, makeGoodKey(theKey));
    }

    /**
     * Create a new EncryptedFile
     * @param theFileURI the file URI to read/write to
     * @param theKey the key to use for encryption/decryption
     * @throws IllegalStateException thrown if there is an error creating a cipher
     */
    public EncryptedFile(URI theFileURI, String theKey) throws IllegalStateException {
        this(theFileURI, makeGoodKey(theKey));
    }

    /**
     * Create a new EncryptedFile
     * @param theFileURI the file URI to read/write to
     * @param theKey the key to use for encryption/decryption
     * @throws IllegalStateException thrown if there is an error creating a cipher
     */
    public EncryptedFile(URI theFileURI, SecretKey theKey) throws IllegalStateException {
        super(theFileURI);

        initCiphers(theKey);
    }

    /**
     * Create a new EncryptedFile
     * @param theFile the file to read/write to
     * @param theKey the key to use for encryption/decryption
     * @throws IllegalStateException thrown if there is an error creating a cipher.
     */
    public EncryptedFile(String theFile, SecretKey theKey) throws IllegalStateException {
        super(theFile);

        initCiphers(theKey);
    }

    /**
     * Initialize the ciphers based on the given secret key
     * @param theKey the key to use to create the ciphers
     */
    private void initCiphers(SecretKey theKey) {
        AlgorithmParameterSpec aSpec = new IvParameterSpec(IV);

         try {
             mEncryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

             // CBC requires an initialization vector
             mEncryptCipher.init(Cipher.ENCRYPT_MODE, theKey, aSpec);

             mDecryptCipher = createDecryptCipher(theKey);
         }
         catch (java.security.InvalidAlgorithmParameterException e) {
             throw new IllegalStateException(e);
         }
         catch (javax.crypto.NoSuchPaddingException e) {
             throw new IllegalStateException(e);
         }
         catch (java.security.NoSuchAlgorithmException e) {
             throw new IllegalStateException(e);
         }
         catch (java.security.InvalidKeyException e) {
             throw new IllegalStateException(e);
         }
    }

    /**
     * Returns an InputStream that will decrypt the bytes coming in through the specified input using the given key
     * @param theStream the stream to read encrypted data from
     * @param theKey the key to use for decryption
     * @return an input stream that will decrypt the data
     * @throws IOException thrown  if the stream cannot be read from, or if there is an error while decrypting
     */
    public static InputStream decrypt(InputStream theStream, String theKey) throws IOException {
        return decrypt(theStream, makeGoodKey(theKey));
    }

    /**
     * Returns an inputstream that will decrypt the bytes coming in through the specified input using the given key
     * @param theStream the stream to read encrypted data from
     * @param theKey the key to use for decryption
     * @return an input stream that will decrypt the data
     * @throws IOException if the stream cannot be read from, or if there is an error while decrypting
     */
    public static InputStream decrypt(InputStream theStream, SecretKey theKey) throws IOException {
        try {
            return new CipherInputStream(theStream, createDecryptCipher(theKey));
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new IOException(e.getMessage());
        }
        catch (InvalidKeyException e) {
            throw new IOException(e.getMessage());
        }
        catch (NoSuchAlgorithmException e) {
            throw new IOException(e.getMessage());
        }
        catch (NoSuchPaddingException e) {
            throw new IOException(e.getMessage());
        }
    }

	/**
	 * Return an encrypted output stream using the given key
	 * @param theStream the stream to encrypt
	 * @param theKey the key to use for encryption
	 * @return an encrypted stream
	 * @throws IOException thrown if there is an error while encrypting
	 */
    public static OutputStream encrypt(OutputStream theStream, String theKey) throws IOException {
        return encrypt(theStream, makeGoodKey(theKey));
    }

	/**
	 * Return an encrypted output stream using the given secret key
	 * @param theStream the stream to encrypt
	 * @param theKey the key to use for encryption
	 * @return an encrypted streawm
	 * @throws IOException thrown if there is an error while encrypting the stream
	 */
    public static OutputStream encrypt(OutputStream theStream, SecretKey theKey) throws IOException {
        try {
            return new CipherOutputStream(theStream, createEncryptCipher(theKey));
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new IOException(e.getMessage());
        }
        catch (InvalidKeyException e) {
            throw new IOException(e.getMessage());
        }
        catch (NoSuchAlgorithmException e) {
            throw new IOException(e.getMessage());
        }
        catch (NoSuchPaddingException e) {
            throw new IOException(e.getMessage());
        }
    }

    
    /**
     * Create a decryption cipher
     * @param theKey the key to use for the cipher
     * @return the decryption cipher
     * @throws InvalidAlgorithmParameterException thrown if the algorithm parameter is bad
     * @throws InvalidKeyException thrown if the provided key is not valid
     * @throws NoSuchAlgorithmException thrown if the request cipher algorithm is not available
     * @throws NoSuchPaddingException thrown if there's no padding method
     */
    private static Cipher createDecryptCipher(SecretKey theKey) throws InvalidAlgorithmParameterException,
                                                                       InvalidKeyException, NoSuchAlgorithmException,
                                                                       NoSuchPaddingException {
        Cipher aCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        aCipher.init(Cipher.DECRYPT_MODE, theKey, new IvParameterSpec(IV));
        return aCipher;
    }
    
    /**
     * Create an encryption cipher
     * @param theKey the key to use for the cipher
     * @return the decryption cipher
     * @throws InvalidAlgorithmParameterException thrown if the algorithm parameter is bad
     * @throws InvalidKeyException thrown if the provided key is not valid
     * @throws NoSuchAlgorithmException thrown if the request cipher algorithm is not available
     * @throws NoSuchPaddingException thrown if there's no padding method
     */
    private static Cipher createEncryptCipher(SecretKey theKey) throws InvalidAlgorithmParameterException,
                                                                       InvalidKeyException, NoSuchAlgorithmException,
                                                                       NoSuchPaddingException {
        Cipher aCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        aCipher.init(Cipher.ENCRYPT_MODE, theKey, new IvParameterSpec(IV));
        return aCipher;
    }

    /**
     * Given a string key, either trim it down so its an appropriate size, or pad it to make it long enough
     * @param theKey the key use
     * @return a secret key based on the provided key string.
     */
    private static SecretKey makeGoodKey(String theKey) {
        String aKey = theKey;
        if (aKey.length() > 8) {
            aKey = aKey.substring(0, 8);
        }
        else if (aKey.length() < 8) {
            while (aKey.length() < 8) {
                aKey += "0";
            }
        }

        return new SecretKeySpec(aKey.getBytes(), "DES");
    }

    /**
     * Open an input stream to read from the encrypted file
     * @return an input stream for the encrypted file
     * @throws IOException thrown if there is an error reading from the file
     */
    public InputStream getInputStream() throws IOException {
        return new CipherInputStream(new FileInputStream(this), mDecryptCipher);
    }

    /**
     * Open an output stream to write data to an encrypted file.
     * @return the stream to write to
     * @throws IOException thrown if there is an error writing to the file
     */
    public OutputStream getOutputStream() throws IOException {
        return new CipherOutputStream(new FileOutputStream(this), mEncryptCipher);
    }
}

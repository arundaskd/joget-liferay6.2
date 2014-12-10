package org.joget.plugin.liferay.util;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.liferay.portal.PwdEncryptorException;
import com.liferay.portal.kernel.io.BigEndianCodec;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

public class PBKDF2EncryptionConfiguration {
	
	private static final int _KEY_SIZE = 160;

	private static final int _ROUNDS = 128000;

	private static final int _SALT_BYTES_LENGTH = 8;

	private static final Pattern _pattern = Pattern.compile(
		"^.*/?([0-9]+)?/([0-9]+)$");
	public void configure(String algorithm, String encryptedPassword)
			throws PwdEncryptorException {

			if (Validator.isNull(encryptedPassword)) {
				Matcher matcher = _pattern.matcher(algorithm);

				if (matcher.matches()) {
					_keySize = GetterUtil.getInteger(
						matcher.group(1), _KEY_SIZE);

					_rounds = GetterUtil.getInteger(matcher.group(2), _ROUNDS);
				}

				BigEndianCodec.putLong(
					_saltBytes, 0, SecureRandomUtil.nextLong());
			}
			else {
				byte[] bytes = new byte[16];

				try {
					byte[] encryptedPasswordBytes = Base64.decode(
						encryptedPassword);

					System.arraycopy(
						encryptedPasswordBytes, 0, bytes, 0, bytes.length);
				}
				catch (Exception e) {
					throw new PwdEncryptorException(
						"Unable to extract salt from encrypted password " +
							e.getMessage(),
						e);
				}

				ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

				_keySize = byteBuffer.getInt();
				_rounds = byteBuffer.getInt();

				byteBuffer.get(_saltBytes);
			}
		}

		public int getKeySize() {
			return _keySize;
		}

		public int getRounds() {
			return _rounds;
		}

		public byte[] getSaltBytes() {
			return _saltBytes;
		}

		private int _keySize = _KEY_SIZE;
		private int _rounds = _ROUNDS;
		private final byte[] _saltBytes = new byte[_SALT_BYTES_LENGTH];

}

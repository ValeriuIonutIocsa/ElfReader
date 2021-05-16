package com.conti.elf_reader.data_parsers.dwarf.data.abbrev.entries;

import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.conti.elf_reader.data_parsers.dwarf.data.code_maps.DwAtType;
import com.conti.elf_reader.data_parsers.dwarf.data.code_maps.DwFormType;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.conti.elf_reader.utils.data_types.ObjectWrapper;

public class AbbrevEntry {

	public static final ObjectWrapper<Integer> importedUnitOffsetWrapper = new ObjectWrapper<>();
	public static final ObjectWrapper<String> nameWrapper = new ObjectWrapper<>();
	public static final ObjectWrapper<Long> startAddressWrapper = new ObjectWrapper<>();
	public static final ObjectWrapper<Long> endAddressWrapper = new ObjectWrapper<>();
	public static final ObjectWrapper<Integer> typeDieOffsetWrapper = new ObjectWrapper<>();
	public static final ObjectWrapper<Boolean> isExternalWrapper = new ObjectWrapper<>();
	public static final ObjectWrapper<Boolean> isDeclarationWrapper = new ObjectWrapper<>();
	public static final ObjectWrapper<Integer> declarationFileIndexWrapper = new ObjectWrapper<>();

	private final DwAtType dwAtType;
	private final DwFormType dwFormType;

	AbbrevEntry(final DwAtType dwAtType, final DwFormType dwFormType) {

		this.dwAtType = dwAtType;
		this.dwFormType = dwFormType;
	}

	public void parseAttribute(
			final ByteBuffer byteBuffer, final ByteBuffer debugStrByteBuffer,
			final int compilationUnitAddress, final int offset,
			final Map<Integer, Integer> typeReferenceMap, final Map<Integer, Integer> typeByteSizeMap) {

		final Object value = readAttributeValue(
				byteBuffer, debugStrByteBuffer, dwFormType, compilationUnitAddress);
		if (value == null || dwAtType == null) {
			return;
		}

		switch (dwAtType) {

			case DW_AT_import: {
				final int importedUnitOffsetValue = (int) value;
				importedUnitOffsetWrapper.setValue(importedUnitOffsetValue);
				break;
			}

			case DW_AT_name: {
				final String name = value.toString();
				nameWrapper.setValue(name);
				break;
			}

			case DW_AT_external: {
				final boolean isExternal = StringUtils.equals(value.toString(), "1");
				isExternalWrapper.setValue(isExternal);
				break;
			}

			case DW_AT_declaration: {
				final boolean isDeclaration = StringUtils.equals(value.toString(), "1");
				isDeclarationWrapper.setValue(isDeclaration);
				break;
			}

			case DW_AT_decl_file: {
				final int declarationFileIndex = Integer.parseInt(value.toString());
				declarationFileIndexWrapper.setValue(declarationFileIndex);
				break;
			}

			case DW_AT_location: {
				final long startAddress = parseDwAtLocation(value, dwFormType);
				startAddressWrapper.setValue(startAddress);
				break;
			}

			case DW_AT_byte_size: {
				if (value instanceof Integer || value instanceof Long) {
					final int typeByteSize = ((Number) value).intValue();
					typeByteSizeMap.put(offset, typeByteSize);
				}
				break;
			}

			case DW_AT_type: {
				final int typeDieOffset = (int) value;
				typeReferenceMap.put(offset, typeDieOffset);
				typeDieOffsetWrapper.setValue(typeDieOffset);
				break;
			}

			case DW_AT_low_pc: {
				final long startAddress = (int) value & 0xFFFFFFFFL;
				startAddressWrapper.setValue(startAddress);
				break;
			}

			case DW_AT_high_pc: {
				final long endAddress = (int) value & 0xFFFFFFFFL;
				endAddressWrapper.setValue(endAddress);
				break;
			}

			default:
				break;
		}
	}

	private static Object readAttributeValue(
			final ByteBuffer byteBuffer, final ByteBuffer debugStrByteBuffer,
			final DwFormType dwFormType, final int compilationUnitAddress) {

		if (dwFormType == null) {
			return null;
		}

		switch (dwFormType) {

			case DW_FORM_string:
				return DataTypes.parseNullTerminatedString(byteBuffer);

			case DW_FORM_addr:
				return byteBuffer.getInt();

			case DW_FORM_strp: {
				try {
					final int offset = byteBuffer.getInt();
					debugStrByteBuffer.position(offset);
					return DataTypes.parseNullTerminatedString(debugStrByteBuffer);
				} catch (final Exception ignored) {
				}
			}

			case DW_FORM_data1:
				return byteBuffer.get() & 0xff;

			case DW_FORM_data2:
				return byteBuffer.getShort();

			case DW_FORM_data4:
				return byteBuffer.getInt();

			case DW_FORM_data8:
				return byteBuffer.getLong();

			case DW_FORM_ref_addr:
				return byteBuffer.getInt();

			case DW_FORM_ref1:
				return byteBuffer.get() + compilationUnitAddress;

			case DW_FORM_ref2:
				return byteBuffer.getShort() + compilationUnitAddress;

			case DW_FORM_ref4:
				return byteBuffer.getInt() + compilationUnitAddress;

			case DW_FORM_ref8:
				return byteBuffer.getLong() + compilationUnitAddress;

			case DW_FORM_ref_udata:
				return DataTypes.parseUnsignedLittleEndianBase128(byteBuffer);

			case DW_FORM_flag:
				return byteBuffer.get();

			case DW_FORM_sec_offset:
				return byteBuffer.getInt();

			case DW_FORM_flag_present:
				return 1;

			case DW_FORM_sdata:
				return DataTypes.parseSignedLittleEndianBase128(byteBuffer);

			case DW_FORM_udata:
				return DataTypes.parseUnsignedLittleEndianBase128(byteBuffer);

			case DW_FORM_block: {
				final int size = (int) DataTypes.parseUnsignedLittleEndianBase128(byteBuffer);
				return getByteBuffer(byteBuffer, size);
			}

			case DW_FORM_block1: {
				final int size = byteBuffer.get() & 0xff;
				return getByteBuffer(byteBuffer, size);
			}

			case DW_FORM_block2: {
				final short size = byteBuffer.getShort();
				return getByteBuffer(byteBuffer, size);
			}

			case DW_FORM_block4: {
				final int size = byteBuffer.getInt();
				return getByteBuffer(byteBuffer, size);
			}

			case DW_FORM_exprloc: {
				final long size = DataTypes.parseUnsignedLittleEndianBase128(byteBuffer);
				return getByteBuffer(byteBuffer, (int) size);
			}

			default:
				return null;
		}
	}

	private static ByteBuffer getByteBuffer(final ByteBuffer byteBufferSource, final int size) {

		try {
			final byte[] data = new byte[size];
			byteBufferSource.get(data);

			final ByteBuffer result = ByteBuffer.wrap(data);
			result.order(byteBufferSource.order());
			return result;

		} catch (final Exception ignored) {
		}
		return null;
	}

	private static long parseDwAtLocation(final Object value, final DwFormType entryFormType) {

		switch (entryFormType) {

			case DW_FORM_block:
			case DW_FORM_block1:
				return parseDwFormBlock(value);

			case DW_FORM_data4:
				return (int) value & 0xFFFFFFFFL;

			default:
				return -1;
		}
	}

	private static long parseDwFormBlock(final Object value) {

		if (!(value instanceof ByteBuffer)) {
			return -1;
		}

		final ByteBuffer valueBuffer = (ByteBuffer) value;
		if (valueBuffer.remaining() == 5) {
			valueBuffer.get();
			return valueBuffer.getInt() & 0xFFFFFFFFL;
		}

		return -1;
	}
}

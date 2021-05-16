package com.conti.elf_reader.data_parsers.dwarf.data.code_maps;

public enum DwFormType {

	DW_FORM_addr(0x0001),
	DW_FORM_block2(0x0003),
	DW_FORM_block4(0x0004),
	DW_FORM_data2(0x0005),
	DW_FORM_data4(0x0006),
	DW_FORM_data8(0x0007),
	DW_FORM_string(0x0008),
	DW_FORM_block(0x0009),
	DW_FORM_block1(0x000a),
	DW_FORM_data1(0x000b),
	DW_FORM_flag(0x000c),
	DW_FORM_sdata(0x000d),
	DW_FORM_strp(0x000e),
	DW_FORM_udata(0x000f),
	DW_FORM_ref_addr(0x0010),
	DW_FORM_ref1(0x0011),
	DW_FORM_ref2(0x0012),
	DW_FORM_ref4(0x0013),
	DW_FORM_ref8(0x0014),
	DW_FORM_ref_udata(0x0015),
	DW_FORM_sec_offset(0x0017),
	DW_FORM_exprloc(0x0018),
	DW_FORM_flag_present(0x0019);

	private final int value;

	DwFormType(int value) {

		this.value = value;
	}

	public static DwFormType byValue(int value) {

		for (DwFormType dwFormType : DwFormType.values()) {
			if (dwFormType.value == value)
				return dwFormType;
		}
		return null;
	}

	public int value() {
		return this.value;
	}
}

/*!
 * File generated by platform wizard. DO NOT MODIFY
 *
 */

metaonly module Platform inherits xdc.platform.IPlatform {

    config ti.platforms.generic.Platform.Instance CPU =
        ti.platforms.generic.Platform.create("CPU", {
            clockRate:      1000,                                       
            catalogName:    "ti.catalog.c6000",
            deviceName:     "TMS320C6678",
            customMemoryMap:
           [          
                ["MSMCSRAM", 
                     {
                        name: "MSMCSRAM",
                        base: 0x0c000000,                    
                        len: 0x00200000,                    
                        space: "code/data",
                        access: "RWX",
                     }
                ],
                ["L2SRAM", 
                     {
                        name: "L2SRAM",
                        base: 0x00800000,                    
                        len: 0x00080000,                    
                        space: "code/data",
                        access: "RWX",
                     }
                ],
                ["DDR3", 
                     {
                        name: "DDR3",
                        base: 0x80000000,                    
                        len: 0x20000000,                    
                        space: "code/data",
                        access: "RWX",
                     }
                ],
                ["L2SRAM0", 
                     {
                        name: "L2SRAM0",
                        base: 0x10818000,                    
                        len: 0x00068000,                    
                        space: "code/data",
                        access: "RWX",
                     }
                ],
                ["L2SRAM1", 
                     {
                        name: "L2SRAM1",
                        base: 0x11818000,                    
                        len: 0x00068000,                    
                        space: "code/data",
                        access: "RWX",
                     }
                ],
                ["L2SRAM2", 
                     {
                        name: "L2SRAM2",
                        base: 0x12818000,                    
                        len: 0x00068000,                    
                        space: "code/data",
                        access: "RWX",
                     }
                ],
                ["L2SRAM3", 
                     {
                        name: "L2SRAM3",
                        base: 0x13818000,                    
                        len: 0x00068000,                    
                        space: "code/data",
                        access: "RWX",
                     }
                ],
                ["L2SRAM4", 
                     {
                        name: "L2SRAM4",
                        base: 0x14818000,                    
                        len: 0x00068000,                    
                        space: "code/data",
                        access: "RWX",
                     }
                ],
                ["L2SRAM5", 
                     {
                        name: "L2SRAM5",
                        base: 0x15818000,                    
                        len: 0x00068000,                    
                        space: "code/data",
                        access: "RWX",
                     }
                ],
                ["L2SRAM6", 
                     {
                        name: "L2SRAM6",
                        base: 0x16818000,                    
                        len: 0x00068000,                    
                        space: "code/data",
                        access: "RWX",
                     }
                ],
                ["L2SRAM7", 
                     {
                        name: "L2SRAM7",
                        base: 0x17818000,                    
                        len: 0x00068000,                    
                        space: "code/data",
                        access: "RWX",
                     }
                ],
           ],
          l2Mode:"0k",
          l1PMode:"32k",
          l1DMode:"32k",

    });
    
instance :
    
    override config string codeMemory  = "MSMCSRAM";   
    override config string dataMemory  = "L2SRAM";                                
    override config string stackMemory = "L2SRAM";
    
}

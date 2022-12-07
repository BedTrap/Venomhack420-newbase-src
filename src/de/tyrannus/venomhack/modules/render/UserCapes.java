package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.modules.Module;
import net.minecraft.util.Identifier;

public class UserCapes extends Module {
   public static final Identifier TEXTURE = new Identifier("venomhack", "cape.png");
   public static final String[] USER_UUIDS = new String[]{
      "c3924bf3-181b-4f91-a955-34314643cb85",
      "2bcd6a52-dd0a-4e80-b600-e5bce52c74ae",
      "d8734d6b-e8b3-43a3-9ab3-c3a06725405a",
      "a8d4d304-fc48-4356-9666-affd6bb34463",
      "92b269bd-045a-4236-9566-0b645da873c4",
      "fde8c3be-b967-4421-bdeb-2269ed928f26",
      "fa5d1722-08d7-42f0-b342-9df9b550c569",
      "97ee623c-3469-4cd0-bc54-32c91e06dbce",
      "d728defb-4816-48ac-aaf3-77544e984776",
      "16ab62aa-5c27-4f83-900a-303a8d0e0d20",
      "bc3f53ed-e388-4014-8c6f-af7f47817aee",
      "ad23e11f-5920-4815-8203-ba86f165c1de",
      "8da6618b-07a8-42ba-aa12-67d9f70a8037",
      "399896f0-cf30-48fc-bc23-f3e432165694",
      "f938d9f4-c754-4e73-a098-2eb181462077",
      "bf709ceb-be74-4c36-af29-48db101ad136",
      "eed21820-4abf-48f9-9225-eb5473f1d3f5",
      "d5e7ab9e-9b7b-430e-ab57-330fe8c6db3c",
      "8b554333-6c6c-4b82-b109-21edc1e40336",
      "9d4b3cca-bdfe-40af-91fe-60e0399e81cb",
      "3a9debbf-651a-49b6-9ddf-c890ad858803",
      "0a57313a-aa4b-40a5-8753-a0f4940d7ebe",
      "37f5da4a-6942-416d-a28d-6d176de13ba4",
      "67673371-7726-40f0-8823-1d97513c320b",
      "b188b16a-72b1-4d69-bc8d-3dc41da40912",
      "973f17e2-4ad8-4010-8217-6e58faa30f42",
      "0a9e8b65-0256-4484-91a3-1f51ea729760",
      "396f6db1-459a-49d9-bd08-d2e7fa74fe4f",
      "20712fc4-fd46-4974-9d97-0664ac37465e",
      "2b7ae469-222d-4460-aca2-ac20f4755390",
      "86d22296-f286-454e-92c7-a7b3d8dd3c9c",
      "b85a9707-76b2-48e7-b1de-2ee4ba9cc7cb",
      "a85e68a4-2e22-42b1-836e-c5d3c1029185",
      "d44e2067-44fd-448c-856a-fc8f2b5a973f",
      "d03d34fd-b7b1-487a-9886-9a929ce20e6c",
      "40aaf2d5-6788-487a-9985-8a86d7800d1f",
      "702e6ae3-efa8-4aaa-b6ad-97610025f899",
      "c6a7e323-a53e-4d78-8b52-3f9a85adcf8f",
      "74aa831e-72c8-4cb3-938f-cab076ec8c4e",
      "cfe0977d-13a9-4a3a-88fa-e1392bb8334f",
      "ed6fd593-5f9f-477f-9f50-36398002b762",
      "15cbc8bf-0750-41b5-8a30-94fb5e47e41d",
      "2ff2ceca-ce9a-4c30-9f1c-8d6966438cbb",
      "3bff5494-c581-49f6-863e-1a9ca381170b",
      "9536b055-f0ad-4636-b486-52448e8fadcd",
      "bbf6adaf-3233-4345-9ef7-02a8a1ac86ab",
      "a95885f8-6c09-45d8-afef-d9790922e7fa",
      "4ffdccdc-f63d-48c6-bc51-b25ffc2ec355",
      "94ab44b5-c018-4e85-9821-d3ee2f2c6fcc",
      "45a1ff71-dd1c-4547-90ab-a42d1a1c4f10",
      "d253bf07-4b47-4931-9908-fa864ec814fb",
      "fd23b5d9-e231-4fdb-a356-6dcdb65caa52",
      "da6ed736-cafb-451c-8876-98d5576195e3",
      "4f0e3c55-3a13-46f8-a330-71fe96b148f1",
      "dc55943b-71c3-4987-bf7f-63f691693419"
   };

   public UserCapes() {
      super(Module.Categories.RENDER, "user-capes", "Shows a Venomhack cape on Venomhack users.");
   }
}

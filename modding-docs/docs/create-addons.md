# Create — Patrones Prácticos para Addons

**Complemento de:** `docs/create.md`  
**Loader:** NeoForge 1.21.1  
**Rama de referencia:** `mc1.21.1/dev` · commit `32e0a2c`  
**Addons de referencia:**  
- `createaddition` (mrh0) · commit `85a819e` · Forge 1.20.1 (patrón más cercano disponible)  
- `Steam 'n' Rails` · commit `5ef3a21e` · Multiloader 1.20.1  

> **Nota sobre las ramas de addons:** No existía rama `mc1.21.1` en `MRHminer/createaddition` (repositorio privado/borrado). Se usó `mrh0/createaddition` (Forge 1.20.1) como referencia de patrones. Steam 'n' Rails tampoco tiene rama 1.21.1 aún; se usó su `main` (1.20.1). Los patrones de API son trasladables a 1.21.1/NeoForge con ajustes de capability.

---

## Sección A: Behaviours catalogados

Los Behaviours son componentes que se inyectan en un `SmartBlockEntity` mediante `addBehaviours()`. Create proporciona varios listos para usar en tus addons.

### API base

```java
// BlockEntityBehaviour.java
public abstract class BlockEntityBehaviour {
    public SmartBlockEntity blockEntity;

    public abstract BehaviourType<?> getType();
    public void initialize() {}
    public void tick() {}           // llama a lazyTick() internamente
    public void lazyTick() {}       // se llama cada lazyTickRate ticks (default 10)
    public void setLazyTickRate(int rate) {}
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {}
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {}
    public void destroy() {}        // bloque destruido
    public void unload() {}         // chunk descargado — invalida capabilities

    // Obtener desde fuera del BE:
    public static <T extends BlockEntityBehaviour> T get(BlockGetter reader, BlockPos pos, BehaviourType<T> type)
    public static <T extends BlockEntityBehaviour> T get(BlockEntity be, BehaviourType<T> type)
}
```

> El patrón canónico para obtener desde código externo:  
> `FilteringBehaviour f = BlockEntityBehaviour.get(level, pos, FilteringBehaviour.TYPE);`

---

### A.1 FilteringBehaviour

**Ruta:** `foundation/blockEntity/behaviour/filtering/FilteringBehaviour.java`

Permite al jugador configurar un filtro de items en el bloque usando un slot visual en el mundo. El filtro puede contener un item concreto o un FilterItem (tag, nbt, etc.). Genera el slot 3D en la cara del bloque y maneja la interacción directa del jugador para cambiarlo. También soporta conteo (cantidad exacta vs "hasta N").

**Constructor:**
```java
public FilteringBehaviour(SmartBlockEntity be, ValueBoxTransform slot)
```

**Métodos clave:**
```java
// Configuración al construir (builder pattern):
.withCallback(Consumer<ItemStack> callback)   // llamado cuando cambia el filtro
.withPredicate(Predicate<ItemStack> pred)     // valida qué items se aceptan como filtro
.forRecipes()                                 // modo receta (recipe filter)
.forFluids()                                  // filtra fluidos en lugar de items
.onlyActiveWhen(Supplier<Boolean> condition)  // activa/desactiva dinámicamente
.showCount()                                  // muestra el número de cantidad

// Uso en tiempo de ejecución:
boolean test(ItemStack stack)    // ¿el stack pasa el filtro?
boolean test(FluidStack stack)   // ¿el fluidstack pasa el filtro?
ItemStack getFilter()            // el item de filtro configurado
int getAmount()                  // cantidad configurada (1-64)
boolean setFilter(ItemStack)     // programáticamente (servidor)
```

**Obtener desde fuera:**
```java
FilteringBehaviour filter = BlockEntityBehaviour.get(level, pos, FilteringBehaviour.TYPE);
if (filter != null && filter.test(stack)) { ... }
```

**Ejemplo real — Millstone:**
```java
// sources/create/.../content/kinetics/millstone/MillstoneBlockEntity.java:66
@Override
public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    behaviours.add(new DirectBeltInputBehaviour(this));
    // Millstone no usa FilteringBehaviour (filtra por receta internamente),
    // pero Basin sí lo usa:
}

// Ejemplo de uso real — FunnelBlockEntity:
behaviours.add(filtering = new FilteringBehaviour(this, new FunnelFilterSlot())
    .withCallback(stack -> onFilterChanged())
    .forRecipes());
```

---

### A.2 DirectBeltInputBehaviour

**Ruta:** `content/kinetics/belt/behaviour/DirectBeltInputBehaviour.java`

Permite que el belt deposite items directamente en el bloque en modo "backup-friendly": el belt llama `handleInsertion()` en el BE de destino en lugar de usar capabilities. Esto evita que el belt se atasque si el destino está lleno. Usado por Basin, Saw, Depot, Millstone.

**Constructor:**
```java
public DirectBeltInputBehaviour(SmartBlockEntity be)
```

**Métodos clave:**
```java
.allowingBeltFunnels()                           // permite funnels además de belt directo
.allowingBeltFunnelsWhen(Supplier<Boolean> pred)
.onlyInsertWhen(AvailabilityPredicate pred)      // pred recibe Direction
.considerOccupiedWhen(OccupiedPredicate pred)    // pred recibe Direction
.setInsertionHandler(InsertionCallback callback) // callback(TransportedItemStack, Direction, simulate)

// Uso:
boolean canInsertFromSide(Direction side)
boolean isOccupied(Direction side)
ItemStack handleInsertion(ItemStack stack, Direction side, boolean simulate)
```

**Obtener desde fuera:**
```java
DirectBeltInputBehaviour input = BlockEntityBehaviour.get(level, pos, DirectBeltInputBehaviour.TYPE);
if (input != null && input.canInsertFromSide(Direction.NORTH)) {
    ItemStack remainder = input.handleInsertion(stack, Direction.NORTH, false);
}
```

**Ejemplo real — MillstoneBlockEntity:**
```java
// sources/create/.../millstone/MillstoneBlockEntity.java:65-67
@Override
public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    behaviours.add(new DirectBeltInputBehaviour(this));
    super.addBehaviours(behaviours);
}
```

---

### A.3 SmartFluidTankBehaviour

**Ruta:** `foundation/blockEntity/behaviour/fluid/SmartFluidTankBehaviour.java`

Gestiona uno o varios tanques de fluido con sincronización cliente-servidor lazy (cada 8 ticks por defecto) y animación de nivel interpolada. Registra la capability `IFluidHandler` automáticamente. Incluye dos `BehaviourType` extra para diferenciar entrada y salida: `INPUT` y `OUTPUT`.

**Constructores / Factory:**
```java
// Tank único (más común):
SmartFluidTankBehaviour.single(SmartBlockEntity be, int capacity)

// Multi-tank o tipo personalizado:
new SmartFluidTankBehaviour(BehaviourType<SmartFluidTankBehaviour> type,
                             SmartBlockEntity be, int tanks,
                             int tankCapacity, boolean enforceVariety)
```

**Tipos disponibles:**
```java
SmartFluidTankBehaviour.TYPE    // genérico
SmartFluidTankBehaviour.INPUT   // para entrada (name = "Input")
SmartFluidTankBehaviour.OUTPUT  // para salida  (name = "Output")
```

**Métodos clave:**
```java
.whenFluidUpdates(Runnable callback)  // se llama cuando cambia el fluido
.allowInsertion() / .forbidInsertion()
.allowExtraction() / .forbidExtraction()

// Acceso:
IFluidHandler getCapability()         // handler combinado (expuesto externamente)
SmartFluidTank getPrimaryHandler()    // el tanque principal (slot 0)
TankSegment getPrimaryTank()          // acceso a nivel renderizado + lerp
TankSegment[] getTanks()
boolean isEmpty()
void sendDataImmediately()            // fuerza sync al cliente ya
void sendDataLazily()                 // sync en próximo ciclo
```

**TankSegment** expone `LerpedFloat getFluidLevel()` y `FluidStack getRenderedFluid()` para el renderer.

**Obtener desde fuera:**
```java
SmartFluidTankBehaviour tank = BlockEntityBehaviour.get(level, pos, SmartFluidTankBehaviour.TYPE);
// o por tipo:
SmartFluidTankBehaviour input = BlockEntityBehaviour.get(level, pos, SmartFluidTankBehaviour.INPUT);
```

**Ejemplo real (Basin usa SmartFluidTankBehaviour.INPUT y OUTPUT):**
```java
// Patrón típico en addons:
@Override
public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    behaviours.add(fluidTank = SmartFluidTankBehaviour.single(this, 1000)
        .whenFluidUpdates(this::onFluidChanged));
}
```

---

### A.4 ScrollValueBehaviour

**Ruta:** `foundation/blockEntity/behaviour/scrollValue/ScrollValueBehaviour.java`

Muestra un widget numérico en el mundo (tipo "dial") que el jugador puede ajustar haciendo scroll con la rueda del ratón mientras tiene una llave inglesa. Guarda un entero en NBT. Usado por la Creative Motor para la velocidad.

**Constructor:**
```java
public ScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot)
```

**Métodos clave:**
```java
.between(int min, int max)            // rango del valor
.withCallback(Consumer<Integer> cb)   // llamado en servidor al cambiar
.withClientCallback(Consumer<Integer> cb)
.withFormatter(Function<Integer, String> f)  // cómo se muestra el número
.requiresWrench()                     // oculto sin llave inglesa
.onlyActiveWhen(Supplier<Boolean> condition)

void setValue(int value)
int getValue()
String formatValue()
```

**Obtener desde fuera:**
```java
ScrollValueBehaviour scroll = BlockEntityBehaviour.get(level, pos, ScrollValueBehaviour.TYPE);
int val = scroll.getValue();
```

**Ejemplo real — ElectricMotorBlockEntity (createaddition):**
```java
// sources/createaddition/.../electric_motor/ElectricMotorBlockEntity.java
CenteredSideValueBoxTransform slot = new CenteredSideValueBoxTransform(
    (motor, side) -> motor.getValue(ElectricMotorBlock.FACING) == side.getOpposite());

generatedSpeed = new KineticScrollValueBehaviour(
    CreateLang.translateDirect("generic.speed"), this, slot);
generatedSpeed.between(-Config.ELECTRIC_MOTOR_RPM_RANGE.get(), Config.ELECTRIC_MOTOR_RPM_RANGE.get());
generatedSpeed.value = 32;
generatedSpeed.withCallback(i -> this.updateGeneratedRotation(i));
behaviours.add(generatedSpeed);
```

---

### A.5 ScrollOptionBehaviour\<E extends Enum\<E\> & INamedIconOptions\>

**Ruta:** `foundation/blockEntity/behaviour/scrollValue/ScrollOptionBehaviour.java`

Extiende `ScrollValueBehaviour` para ciclar entre opciones de un enum. El widget muestra el icono y nombre de cada opción. Tu enum debe implementar `INamedIconOptions`.

**Constructor:**
```java
public ScrollOptionBehaviour(Class<E> enum_, Component label,
                              SmartBlockEntity be, ValueBoxTransform slot)
```

**Métodos clave:**
```java
E get()                    // la opción actualmente seleccionada
INamedIconOptions getIconForSelected()
```

**INamedIconOptions** requiere:
```java
ResourceLocation getIconLocation();
String getTranslationKey();
```

**Ejemplo de uso:**
```java
public enum Mode implements INamedIconOptions {
    ACCEPT, REJECT;
    // implementa métodos de la interfaz...
}

behaviours.add(mode = new ScrollOptionBehaviour<>(Mode.class,
    Component.translatable("mymod.mode"), this, new CenteredSideValueBoxTransform()));
// Más tarde:
if (mode.get() == Mode.ACCEPT) { ... }
```

---

### A.6 EdgeInteractionBehaviour

**Ruta:** `foundation/blockEntity/behaviour/edgeInteraction/EdgeInteractionBehaviour.java`

Permite conectar dos bloques adyacentes por sus aristas (bordes) usando un item específico, generando un callback. Usado por los Mechanical Belts para conectarse a poleas.

**Constructor:**
```java
public EdgeInteractionBehaviour(SmartBlockEntity be, ConnectionCallback callback)
// callback: (Level world, BlockPos clicked, BlockPos neighbour) -> void
```

**Métodos clave:**
```java
.require(Item required)                // item necesario para interactuar
.require(Predicate<Item> predicate)
.connectivity(ConnectivityPredicate p) // p: (world, pos, selectedFace, connectedFace) -> bool
```

**Obtener desde fuera:**
```java
EdgeInteractionBehaviour edge = BlockEntityBehaviour.get(level, pos, EdgeInteractionBehaviour.TYPE);
```

> Este Behaviour no es frecuente en addons de máquinas normales; es más relevante para mecanismos de transmisión de par.

---

### A.7 CenteredSideValueBoxTransform

**Ruta:** `foundation/blockEntity/behaviour/CenteredSideValueBoxTransform.java`

No es un Behaviour en sí, sino un `ValueBoxTransform.Sided` que posiciona el widget de cualquier Behaviour en el centro de una cara del bloque. Es el más usado en addons.

**Constructores:**
```java
public CenteredSideValueBoxTransform()                               // todas las caras activas
public CenteredSideValueBoxTransform(BiPredicate<BlockState, Direction> allowedDirections)
```

**Ejemplo — solo la cara FACING del motor:**
```java
new CenteredSideValueBoxTransform(
    (state, side) -> state.getValue(MyBlock.FACING) == side.getOpposite())
```

---

### A.8 InvManipulationBehaviour

**Ruta:** `foundation/blockEntity/behaviour/inventory/InvManipulationBehaviour.java`

Accede al `IItemHandler` de un bloque vecino para extraer o insertar items. La dirección se resuelve dinámicamente vía `InterfaceProvider`. Integra automáticamente con `FilteringBehaviour` si el BE lo tiene.

**Tipos disponibles:**
```java
InvManipulationBehaviour.TYPE     // genérico
InvManipulationBehaviour.EXTRACT  // extrae del vecino
InvManipulationBehaviour.INSERT   // inserta en el vecino
```

**Factories:**
```java
InvManipulationBehaviour.forExtraction(SmartBlockEntity be, InterfaceProvider target)
InvManipulationBehaviour.forInsertion(SmartBlockEntity be, InterfaceProvider target)
```

**Métodos clave:**
```java
ItemStack extract()                              // extrae según filtro
ItemStack extract(ExtractionCountMode, int)
ItemStack extract(ExtractionCountMode, int, Predicate<ItemStack> filter)
ItemStack insert(ItemStack stack)
boolean hasInventory()
IItemHandler getInventory()
```

`InterfaceProvider` es un functional interface `(SmartBlockEntity) -> BlockPos` que devuelve la posición del vecino.

---

### A.9 VersionedInventoryTrackerBehaviour

**Ruta:** `foundation/blockEntity/behaviour/inventory/VersionedInventoryTrackerBehaviour.java`

Rastreador de versión de inventario para evitar polling innecesario. Funciona con `VersionedInventoryWrapper`; permite saber si el inventario de un vecino ha cambiado desde la última extracción.

**Constructor:**
```java
public VersionedInventoryTrackerBehaviour(SmartBlockEntity be)
```

**Métodos clave:**
```java
boolean stillWaiting(InvManipulationBehaviour behaviour)  // ¿sigue esperando cambio?
boolean stillWaiting(IItemHandler handler)
void awaitNewVersion(InvManipulationBehaviour behaviour)  // marca "esperando"
void awaitNewVersion(IItemHandler handler)
void reset()
```

**Patrón de uso:**
```java
// En tick():
if (invVersionTracker.stillWaiting(invBehaviour)) return;
ItemStack extracted = invBehaviour.extract();
if (extracted.isEmpty()) {
    invVersionTracker.awaitNewVersion(invBehaviour);
    return;
}
```

---

### A.10 DeferralBehaviour

**Ruta:** `foundation/blockEntity/behaviour/simple/DeferralBehaviour.java`

Aplaza una tarea hasta que un callback devuelva `true`. Persiste el estado pendiente en NBT, por lo que sobrevive a recargas del chunk. Útil para operaciones que requieren que el mundo esté completamente cargado.

**Constructor:**
```java
public DeferralBehaviour(SmartBlockEntity be, Supplier<Boolean> callback)
// callback: devuelve true cuando la tarea se completó con éxito
```

**Métodos clave:**
```java
void scheduleUpdate()   // programa la tarea
// No hay getters adicionales; el callback se llama en tick() automáticamente
```

**Ejemplo:**
```java
behaviours.add(deferral = new DeferralBehaviour(this, () -> {
    // Intenta conectar con el bloque vecino; devuelve true si lo logra
    return connectToNetwork();
}));
// Cuando necesites aplazar:
deferral.scheduleUpdate();
```

**Obtener desde fuera:**
```java
DeferralBehaviour d = BlockEntityBehaviour.get(level, pos, DeferralBehaviour.TYPE);
d.scheduleUpdate();
```

---

### A.11 AnimatedContainerBehaviour

**Ruta:** `foundation/blockEntity/behaviour/animatedContainer/AnimatedContainerBehaviour.java`

Permite que un BE tenga un contenedor con animación de apertura/cierre (como un cofre). Gestiona el conteo de jugadores y el ángulo interpolado de apertura.

> Útil si tu addon tiene un bloque tipo cofre rotativo.

---

### Behaviours no encontrados / no aplicables

| Nombre solicitado | Estado |
|---|---|
| `TransportedItemStackHandlerBehaviour` | **No existe** en el código de Create. El manejo de `TransportedItemStack` se hace directamente dentro de Belt/DirectBeltInputBehaviour. |
| `FluidTankBehaviour` (sin "Smart") | **No existe** como clase separada. Solo existe `SmartFluidTankBehaviour`. |
| `ChassisRangeDisplay` | **No existe**. El display del rango de Chassis se gestiona con `ValueBox` directamente en el renderer del Chassis. |

---

## Sección B: Sistema de Stress en detalle

> Para la API general de stress, ver `create.md §4` (KineticNetwork, speed propagation). Esta sección se enfoca en cómo un **addon** declara sus propios valores.

### B.1 API pública

**Archivo:** `api/stress/BlockStressValues.java`

```java
public class BlockStressValues {
    // Registra el impacto base a 1 RPM (consumidor)
    public static final SimpleRegistry<Block, DoubleSupplier> IMPACTS = SimpleRegistry.create();
    // Registra la capacidad base a 1 RPM (generador)
    public static final SimpleRegistry<Block, DoubleSupplier> CAPACITIES = SimpleRegistry.create();
    // Solo para tooltips: RPM que genera
    public static final SimpleRegistry<Block, GeneratedRpm> RPM = SimpleRegistry.create();

    public static double getImpact(Block block)   // 0 si no registrado
    public static double getCapacity(Block block) // 0 si no registrado

    // Utility para Registrate:
    public static NonNullConsumer<Block> setGeneratorSpeed(int value)
    public static NonNullConsumer<Block> setGeneratorSpeed(int value, boolean mayGenerateLess)

    public record GeneratedRpm(int value, boolean mayGenerateLess) {}
}
```

### B.2 Cómo Create declara sus propios valores

Create usa su config interna (`CStress`) y lo registra como **provider** en `AllConfigs`:

```java
// infrastructure/config/AllConfigs.java
BlockStressValues.IMPACTS.registerProvider(stress::getImpact);
BlockStressValues.CAPACITIES.registerProvider(stress::getCapacity);
```

`CStress` lee los valores de la config de usuario (archivo `.toml`). Los defaults se definen con las utilidades de Registrate:

```java
// infrastructure/config/CStress.java
public static NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double value) { ... }
public static NonNullUnaryOperator<BlockBuilder<B, P>> setCapacity(double value) { ... }
```

> **Los addons NO deben usar `CStress`** (es solo para Create). Los addons usan `IMPACTS.register()` directamente.

### B.3 Cómo declara su impacto un addon (máquina consumidora)

```java
// En tu clase de inicialización o en tu bloque, durante mod init:
BlockStressValues.IMPACTS.register(MyBlocks.MY_MACHINE.get(), () -> 4.0);
// → tu máquina consume 4 SU por RPM
```

El **coste total** en SU a velocidad `v` es: `impact × |v|`.  
Esto lo calcula `KineticBlockEntity.calculateStressApplied()`:

```java
// KineticBlockEntity.java:179-183
public float calculateStressApplied() {
    float impact = (float) BlockStressValues.getImpact(getStressConfigKey());
    this.lastStressApplied = impact;
    return impact;
}
// El sistema multiplica este valor por |getTheoreticalSpeed()| al contabilizar la red.
```

Para anular el comportamiento por bloque específico:
```java
@Override
protected Block getStressConfigKey() {
    return MyBlocks.MY_MACHINE.get(); // default: getBlockState().getBlock()
}

@Override
public float calculateStressApplied() {
    float base = 4.0f; // SU por RPM
    this.lastStressApplied = base;
    return base;
}
```

### B.4 Cómo declara su capacidad un addon (generador)

```java
// Registro:
BlockStressValues.CAPACITIES.register(MyBlocks.MY_GENERATOR.get(), () -> 256.0);
// → tu generador provee 256 SU por RPM

// Para tooltips, indicar los RPM que genera:
BlockStressValues.RPM.register(MyBlocks.MY_GENERATOR.get(),
    new BlockStressValues.GeneratedRpm(64, false)); // 64 RPM fijo
```

Tu BlockEntity debe extender `GeneratingKineticBlockEntity`:

```java
public class MyGeneratorBlockEntity extends GeneratingKineticBlockEntity {

    @Override
    public float getGeneratedSpeed() {
        return 64f; // RPM que produce (puede ser dinámico)
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = 256.0f / 64f; // SU base (se escala por speed internamente)
        this.lastCapacityProvided = capacity;
        return capacity;
    }
}
```

> **Importante:** `calculateAddedStressCapacity()` debe devolver la capacidad **base a 1 RPM** (o la capacidad total — Create la escala correctamente). Cuando la velocidad real difiere de `getGeneratedSpeed()`, `GeneratingKineticBlockEntity` ajusta el ratio automáticamente.

### B.5 Ejemplo concreto: máquina que cuesta 4 SU/RPM

```java
// MyMod.java (en onCommonSetup o similar):
BlockStressValues.IMPACTS.register(MyBlocks.HEAVY_GRINDER.get(), () -> 4.0);

// MyHeavyGrinderBlockEntity.java:
public class MyHeavyGrinderBlockEntity extends KineticBlockEntity {
    // No necesitas sobreescribir calculateStressApplied() —
    // KineticBlockEntity lo lee del registro automáticamente.

    // A 32 RPM → 4 × 32 = 128 SU consumidos
    // A 64 RPM → 4 × 64 = 256 SU consumidos
}
```

### B.6 Ejemplo createaddition (ElectricMotorBlockEntity)

El motor eléctrico de createaddition sobreescribe directamente el método:

```java
// sources/createaddition/.../electric_motor/ElectricMotorBlockEntity.java
public float calculateAddedStressCapacity() {
    float capacity = Config.MAX_STRESS.get() / 256f;
    this.lastCapacityProvided = capacity;
    return capacity;
}
```

---

## Sección C: Caso de estudio — MillstoneBlockEntity

La Millstone es una de las máquinas más simples de Create: recibe un item, lo muele durante un tiempo proporcional a la velocidad, y produce outputs.

### C.1 Estructura de archivos del paquete

```
content/kinetics/millstone/
├── MillingRecipe.java           ← tipo de receta, extiende AbstractCrushingRecipe
├── MillstoneBlock.java          ← bloque, extiende KineticBlock + ICogWheel + IBE
├── MillstoneBlockEntity.java    ← lógica, extiende KineticBlockEntity
└── MillstoneRenderer.java       ← renderer, extiende KineticBlockEntityRenderer
```

### C.2 Jerarquía de herencia

```
BlockEntity
  └── SyncedBlockEntity           ← serialización cliente/servidor
        └── SmartBlockEntity      ← sistema de Behaviours + lazyTick
              └── KineticBlockEntity  ← rotación, stress, velocidad
                    └── MillstoneBlockEntity implements Clearable
```

```
Block
  └── KineticBlock               ← shaft, hasShaftTowards()
        └── MillstoneBlock implements IBE<MillstoneBlockEntity>, ICogWheel
```

### C.3 Behaviours utilizados

```java
@Override
public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    behaviours.add(new DirectBeltInputBehaviour(this));   // recibe items del belt
    super.addBehaviours(behaviours);                       // registra AwardableBehaviour
    registerAwardables(behaviours, AllAdvancements.MILLSTONE);
}
```

Solo usa `DirectBeltInputBehaviour`. El filtrado por receta lo hace internamente (no necesita `FilteringBehaviour`).

### C.4 Método tick()

```java
@Override
public void tick() {
    super.tick();               // propaga rotación en la red
    if (getSpeed() == 0) return;

    // Comprueba si output está lleno
    for (int i = 0; i < outputInv.getSlots(); i++)
        if (outputInv.getStackInSlot(i).getCount() == outputInv.getSlotLimit(i)) return;

    if (timer > 0) {
        timer -= getProcessingSpeed(); // getProcessingSpeed() ≈ |speed/16|, clamp(1, 512)
        if (level.isClientSide) {
            spawnParticles(); return;
        }
        if (timer <= 0) process(); // produce outputs
        return;
    }

    // No hay timer activo → busca receta
    if (inputInv.getStackInSlot(0).isEmpty()) return;
    RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);
    // ... localiza y cachea lastRecipe, asigna timer = recipe.getProcessingDuration()
    sendData();
}
```

**`getProcessingSpeed()`**: escala la velocidad de procesado con los RPM. A 16 RPM tarda exactamente `processingDuration` ticks; a 32 RPM termina el doble de rápido.

### C.5 tickAudio() — solo cliente

```java
@Override
@OnlyIn(Dist.CLIENT)
public void tickAudio() {
    super.tickAudio();
    if (getSpeed() == 0) return;
    if (inputInv.getStackInSlot(0).isEmpty()) return;
    float pitch = Mth.clamp((Math.abs(getSpeed()) / 256f) + .45f, .85f, 1f);
    SoundScapes.play(AmbienceGroup.MILLING, worldPosition, pitch);
}
```

### C.6 Sincronización cliente-servidor

`SmartBlockEntity` hereda de `SyncedBlockEntity`, que ofrece:

```java
sendData();      // serializa con write() y envía paquete al cliente
notifyUpdate();  // marca setChanged() + rerenderiza
```

El método `write()` distingue `clientPacket = true` (solo datos para render) de `false` (guardado completo):

```java
@Override
public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
    compound.putInt("Timer", timer);
    compound.put("InputInventory", inputInv.serializeNBT(registries));
    compound.put("OutputInventory", outputInv.serializeNBT(registries));
    super.write(compound, registries, clientPacket);
}
```

### C.7 Capabilities (NeoForge 1.21.1)

```java
public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        Capabilities.ItemHandler.BLOCK,
        AllBlockEntityTypes.MILLSTONE.get(),
        (be, context) -> be.capability   // capability custom que combina input y output
    );
}
```

La `MillstoneInventoryHandler` extiende `CombinedInvWrapper` y sobreescribe `isItemValid`, `insertItem` y `extractItem` para separar input (slot 0) y output (slots 1-9).

### C.8 Receta JSON

```json
// src/generated/resources/data/create/recipe/milling/gravel.json
{
  "type": "create:milling",
  "ingredients": [{ "item": "minecraft:gravel" }],
  "processing_time": 250,
  "results": [{ "id": "minecraft:flint" }]
}
```

Con chance:
```json
{
  "type": "create:milling",
  "ingredients": [{ "item": "minecraft:red_tulip" }],
  "processing_time": 50,
  "results": [
    { "count": 2, "id": "minecraft:red_dye" },
    { "chance": 0.1, "id": "minecraft:lime_dye" }
  ]
}
```

### C.9 Plantilla para tu propia máquina

```java
// MyMachineBlockEntity.java
public class MyMachineBlockEntity extends KineticBlockEntity {
    public ItemStackHandler inputInv;
    public ItemStackHandler outputInv;
    public int timer;
    private MyRecipe lastRecipe;

    public MyMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInv = new ItemStackHandler(1);
        outputInv = new ItemStackHandler(9);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
        super.addBehaviours(behaviours);
    }

    @Override
    public void tick() {
        super.tick();
        if (getSpeed() == 0) return;
        // ... lógica de timer y proceso
    }

    @Override
    public void write(CompoundTag tag, HolderLookup.Provider reg, boolean clientPacket) {
        tag.putInt("Timer", timer);
        tag.put("Input", inputInv.serializeNBT(reg));
        tag.put("Output", outputInv.serializeNBT(reg));
        super.write(tag, reg, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider reg, boolean clientPacket) {
        timer = tag.getInt("Timer");
        inputInv.deserializeNBT(reg, tag.getCompound("Input"));
        outputInv.deserializeNBT(reg, tag.getCompound("Output"));
        super.read(tag, reg, clientPacket);
    }
}
```

---

## Sección D: Recipe Builders Java (datagen)

> Los builders se usan en un `DataGenerator`, no en tiempo de juego.

### D.1 Arquitectura general

```
BaseRecipeProvider (net.minecraft RecipeProvider)
  └── ProcessingRecipeGen<P, R, B>       ← clase base pública del addon API
        └── StandardProcessingRecipeGen<R>  ← para recetas con ProcessingRecipeParams estándar
              ├── MixingRecipeGen
              ├── CrushingRecipeGen
              ├── MillingRecipeGen
              ├── PressingRecipeGen
              ├── CompactingRecipeGen
              ├── CuttingRecipeGen
              ├── DeployingRecipeGen
              ├── FillingRecipeGen
              ├── EmptyingRecipeGen
              ├── WashingRecipeGen       (Splashing)
              ├── HauntingRecipeGen
              ├── PolishingRecipeGen     (SandpaperPolishing)
              └── ItemApplicationRecipeGen
        └── SequencedAssemblyRecipeGen  ← separado, usa su propio builder

ProcessingRecipeBuilder<P, R, S>         ← builder base
  └── StandardProcessingRecipe.Builder<R>
```

**Ruta de los Gen:** `api/data/recipe/`  
**Ruta del Builder:** `content/processing/recipe/ProcessingRecipeBuilder.java`

### D.2 ProcessingRecipeBuilder — métodos comunes

```java
// Ingredientes:
.require(ItemLike item)
.require(TagKey<Item> tag)
.require(Ingredient ingredient)
.require(FlowingFluid fluid, int millibuckets)
.require(TagKey<Fluid> fluidTag, int millibuckets)
.require(SizedFluidIngredient ingredient)

// Outputs items:
.output(ItemLike item)
.output(ItemLike item, int amount)
.output(float chance, ItemLike item, int amount)
.output(ItemStack stack)
.output(float chance, ItemStack stack)

// Outputs fluidos:
.output(Fluid fluid, int millibuckets)
.output(FluidStack fluidStack)

// Timing y condiciones:
.duration(int ticks)
.averageProcessingDuration()      // 100 ticks
.requiresHeat(HeatCondition cond) // NONE, HEATED, SUPERHEATED
.whenModLoaded(String modid)
.whenModMissing(String modid)
.withCondition(ICondition condition)

// Construir:
void build(RecipeOutput consumer)
```

### D.3 Cómo usar en tu RecipeProvider

```java
// En tu DataGenerator:
public class MyProcessingRecipes extends MillingRecipeGen {

    public MyProcessingRecipes(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> reg) {
        super(output, reg, "mymod");
    }

    // Muele "mymod:raw_crystal" → "mymod:crystal_dust" x2 (250 ticks)
    GeneratedRecipe RAW_CRYSTAL = create(MyItems.RAW_CRYSTAL::get, b -> b
        .duration(250)
        .output(MyItems.CRYSTAL_DUST.get(), 2)
        .output(0.2f, MyItems.TINY_DUST.get(), 1));
}

// En tu DataGenerator.gather():
generator.addProvider(event.includeServer(), new MyProcessingRecipes(output, future));
```

### D.4 Ejemplos por tipo de receta

#### Mixing
```java
// MixingRecipeGen
create("molten_steel", b -> b
    .require(Tags.Items.INGOTS_IRON)
    .require(Tags.Items.DUSTS_COAL)
    .require(Fluids.LAVA, 100)
    .output(MyFluids.MOLTEN_STEEL.getSource(), 250)
    .duration(80)
    .requiresHeat(HeatCondition.SUPERHEATED));
```

#### Crushing
```java
// CrushingRecipeGen
create(MyItems.GEODE::get, b -> b
    .duration(400)
    .output(MyItems.GEM.get(), 2)
    .output(0.5f, MyItems.GEM.get(), 1)
    .output(0.25f, MyItems.DUST.get()));
```

#### Pressing
```java
// PressingRecipeGen
create("copper_sheet", b -> b
    .require(Items.COPPER_INGOT)
    .output(MyItems.COPPER_SHEET.get())
    .duration(100));
```

#### Filling (item + fluido → item)
```java
// FillingRecipeGen
create("filled_tank", b -> b
    .require(MyItems.EMPTY_TANK.get())
    .require(MyFluids.OIL.getSource(), 500)
    .output(MyItems.OIL_TANK.get())
    .duration(40));
```

#### Emptying (item → item + fluido)
```java
// EmptyingRecipeGen
create("drain_tank", b -> b
    .require(MyItems.OIL_TANK.get())
    .output(MyItems.EMPTY_TANK.get())
    .output(MyFluids.OIL.getSource(), 500)
    .duration(40));
```

#### Washing (Splashing)
```java
// WashingRecipeGen
create(MyItems.DIRTY_CRYSTAL::get, b -> b
    .output(MyItems.CRYSTAL.get())
    .output(0.25f, Items.SAND)
    .duration(80));
```

#### Haunting
```java
// HauntingRecipeGen
create(Items.SOUL_SAND::get, b -> b
    .output(MyItems.INFUSED_SAND.get())
    .duration(100));
```

#### Compacting
```java
// CompactingRecipeGen
create("compressed_crystal", b -> b
    .require(MyItems.CRYSTAL.get(), 9)   // usa require() 9 veces o un ingrediente con count
    .output(MyItems.CRYSTAL_BLOCK.get())
    .duration(60));
```

#### Deploying (ItemApplicationRecipeGen)
```java
// DeployingRecipeGen (o ItemApplicationRecipeGen)
create("deploy_lens", b -> b
    .require(MyItems.LENS.get())
    .require(MyItems.FRAME.get())
    .output(MyItems.TELESCOPE.get())
    .duration(40));
```

#### SandpaperPolishing
```java
// PolishingRecipeGen
create(MyItems.RAW_GEM::get, b -> b
    .output(MyItems.POLISHED_GEM.get())
    .duration(5));
```

#### Sequenced Assembly
```java
// SequencedAssemblyRecipeGen
create("precision_mechanism_clone", b -> b
    .require(Items.GOLD_INGOT)
    .transitionTo(MyItems.WIP_MECHANISM.get())
    .addStep(PressingRecipe.FACTORY, s -> s)
    .addStep(PressingRecipe.FACTORY, s -> s)
    .addStep(ItemApplicationRecipe.FACTORY, s -> s.require(Items.GOLD_NUGGET))
    .loops(3)
    .addOutput(MyItems.MECHANISM.get(), 8f)
    .addOutput(Items.GOLD_INGOT, 1f));
```

---

## Sección E: MovementBehaviour en detalle

### E.1 Interfaz completa

**Archivo:** `api/behaviour/movement/MovementBehaviour.java`

```java
public interface MovementBehaviour {

    // Registro global:
    SimpleRegistry<Block, MovementBehaviour> REGISTRY = SimpleRegistry.create();

    // Utility para Registrate:
    static <B extends Block> NonNullConsumer<? super B> movementBehaviour(MovementBehaviour b)

    // Ciclo de vida:
    default boolean isActive(MovementContext context)       // default: !context.disabled
    default void startMoving(MovementContext context)       // contraption empieza a moverse
    default void tick(MovementContext context)              // cada tick de la contraption
    default void visitNewPosition(MovementContext context, BlockPos pos) // pasa por pos
    default void onSpeedChanged(MovementContext context, Vec3 oldMotion, Vec3 motion)
    default void stopMoving(MovementContext context)        // contraption parada o desmontada
    default void writeExtraData(MovementContext context)    // escribir datos extra al NBT

    // Comportamiento:
    default Vec3 getActiveAreaOffset(MovementContext context)  // offsets del área de efecto
    default ItemStack canBeDisabledVia(MovementContext context) // item que deshabilita
    default void onDisabledByControls(MovementContext context)
    default boolean mustTickWhileDisabled()

    // Items sobrantes:
    default void collectOrDropItem(MovementContext context, ItemStack stack)

    // Stall:
    default void cancelStall(MovementContext context)

    // Render (solo cliente):
    @OnlyIn(Dist.CLIENT)
    default void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                     ContraptionMatrices matrices, MultiBufferSource buffer)
    @OnlyIn(Dist.CLIENT)
    default ActorVisual createVisual(VisualizationContext ctx, VirtualRenderWorld world,
                                     MovementContext movementContext)

    // Deshabilitar render propio del BE:
    default boolean disableBlockEntityRendering()           // default: false
}
```

### E.2 MovementContext — qué provee

**Archivo:** `content/contraptions/behaviour/MovementContext.java`

```java
public class MovementContext {
    public Vec3 position;           // posición mundial actual del bloque
    public Vec3 motion;             // velocidad de la contraption (mundo)
    public Vec3 relativeMotion;     // velocidad relativa al contraption frame
    public UnaryOperator<Vec3> rotation; // rotación actual del contraption

    public Level world;             // el mundo
    public BlockState state;        // estado del bloque
    public BlockPos localPos;       // posición local dentro de la contraption
    public CompoundTag blockEntityData; // datos NBT del BE original (inmutables)

    public boolean stall;           // si es true, el contraption frena
    public boolean disabled;        // deshabilitado por controles
    public boolean firstMovement;   // primer tick desde que empezó a moverse
    public CompoundTag data;        // almacenamiento mutable para el behaviour
    public Contraption contraption;
    public Object temporaryData;    // datos en memoria no persistentes (cliente/servidor)

    // Storage del contraption (si aplica):
    // context.contraption.getStorage().getAllItems()  → IItemHandler global
}
```

### E.3 Cómo se registra

```java
// Opción 1 — registro directo (en tu clase de init):
MovementBehaviour.REGISTRY.register(MyBlocks.MY_MACHINE.get(), new MyMachineMovementBehaviour());

// Opción 2 — con Registrate (en el builder del bloque):
REGISTRATE.block("my_machine", MyMachineBlock::new)
    .blockEntity(MyMachineBlockEntity::new)
    .onRegister(MovementBehaviour.movementBehaviour(new MyMachineMovementBehaviour()))
    .register();

// Opción 3 — por ResourceLocation (útil para compat con bloques de otros mods):
// Create no lo expone directamente; usa REGISTRY.register(block, behaviour).
```

### E.4 Hacer que tu bloque siga funcionando dentro de un contraption

```java
public class MyMachineMovementBehaviour implements MovementBehaviour {

    @Override
    public void tick(MovementContext context) {
        if (!isActive(context)) return;

        // Leer datos del BE original (solo lectura):
        int timerFromBE = context.blockEntityData.getInt("Timer");

        // Usar data mutable para estado del behaviour:
        int localTimer = context.data.getInt("localTimer");
        localTimer++;
        context.data.putInt("localTimer", localTimer);

        if (localTimer >= 100) {
            context.data.putInt("localTimer", 0);
            // Procesar...
            Vec3 outputPos = context.position.add(0, -1, 0);
            ItemStack drop = new ItemStack(Items.COBBLESTONE);
            collectOrDropItem(context, drop);
        }
    }

    @Override
    public void startMoving(MovementContext context) {
        context.data.putInt("localTimer", 0);
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true; // el BE tiene su propio actor visual
    }
}
```

### E.5 Controlar si el bloque se puede montar en contraption

**Opción 1 — Tags de datos (sin código):**

| Tag | Namespace | Efecto |
|-----|-----------|--------|
| `create:non_movable` | block | El bloque **no puede** montarse en ningún contraption |
| `create:brittle` | block | Se rompe al intentar moverlo (se dropea como item) |
| `create:safe_nbt` | block | El NBT del BE se conserva al montar/desmontar |
| `create:wrench_pickup` | block | Se puede recoger con llave inglesa |

Agregar en `data/create/tags/blocks/`:
```json
{ "replace": false, "values": ["mymod:my_block"] }
```

**Opción 2 — `ContraptionMovementSetting` (código):**

```java
// Bloque completamente inamovible:
ContraptionMovementSetting.REGISTRY.register(MyBlocks.MY_BLOCK.get(),
    () -> ContraptionMovementSetting.UNMOVABLE);

// O implementar la interfaz en el Block:
public class MyBlock extends Block implements ContraptionMovementSetting.MovementSettingProvider {
    @Override
    public ContraptionMovementSetting getContraptionMovementSetting() {
        return ContraptionMovementSetting.MOVABLE; // o UNMOVABLE, NO_PICKUP
    }
}
```

**Opción 3 — `BlockMovementChecks` (checks granulares):**

```java
// Registro de check personalizado (en mod init):
BlockMovementChecks.registerMovementAllowedCheck((state, world, pos) -> {
    if (state.is(MyBlocks.MY_HEAVY_BLOCK.get())) {
        // No se puede mover si hay carga encima
        return world.getBlockState(pos.above()).isSolid()
            ? CheckResult.DENY : CheckResult.PASS;
    }
    return CheckResult.PASS;
});
```

### E.6 Ejemplo real — SmokeStackMovementBehaviour (Steam 'n' Rails)

```java
// sources/railway/.../SmokeStackMovementBehaviour.java
public class SmokeStackMovementBehaviour implements MovementBehaviour {
    // Usa context.data para almacenar estado de animación
    // Emite partículas en renderInContraption() solo en cliente
    // Usa context.motion.length() para determinar si está en movimiento
}

// Registro (via MixinAllBlocks):
MovementBehaviour.REGISTRY.register(smokeStackBlock, new SmokeStackMovementBehaviour());
```

---

## Sección F: Cómo extender máquinas existentes

### F.1 ¿Son finales las clases de Create?

**No**, ninguna clase de máquina de Create es `final`. Puedes extenderlas directamente.

```java
// Ejemplo: Millstone más rápida
public class TurboMillstoneBlockEntity extends MillstoneBlockEntity {
    public TurboMillstoneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public int getProcessingSpeed() {
        return super.getProcessingSpeed() * 2; // el doble de rápido
    }
}
```

> **Riesgo:** si Create cambia el comportamiento interno de los métodos que sobreescribes, tu addon puede romperse en actualizaciones. Usa con cuidado y documenta la versión.

### F.2 Extender GeneratingKineticBlockEntity

```java
// Generador personalizado con capacidad configurable:
public class MyGeneratorBE extends GeneratingKineticBlockEntity {
    private int rpm = 32;

    @Override
    public float getGeneratedSpeed() { return rpm; }

    @Override
    public float calculateAddedStressCapacity() {
        float cap = (float) BlockStressValues.getCapacity(getStressConfigKey());
        this.lastCapacityProvided = cap;
        return cap;
    }
}
```

### F.3 Patrones de createaddition (mrh0)

**Patrón 1 — Extender GeneratingKineticBlockEntity para motor:**
```java
// ElectricMotorBlockEntity extends GeneratingKineticBlockEntity
// Añade ScrollValueBehaviour para el RPM, la EnergyStorage propia
// Override calculateAddedStressCapacity() con config propio
```

**Patrón 2 — ScrollValueBehaviour para RPM controlable:**  
El motor eléctrico usa `KineticScrollValueBehaviour` (subclase de `ScrollValueBehaviour`) con un rango configurable, y en el callback llama `updateGeneratedRotation(newSpeed)`.

**Patrón 3 — Capability NeoForge para energía:**  
createaddition usa `LazyOptional<IEnergyStorage>` (Forge 1.20.1). En NeoForge 1.21.1, el equivalente es el sistema de `RegisterCapabilitiesEvent`.

**Patrón 4 — Config separada:**  
createaddition define su propio `Config` (Forge Config) y no toca la config de Create. Los valores de stress los calcula desde su config.

### F.4 Patrones de Steam 'n' Rails (Railway)

**Patrón 1 — Registrar comportamientos vía Registrate:**  
Railway usa `CreateRegistrate` y registra Behaviours con `onRegister()`.

**Patrón 2 — Mixins para hooks profundos:**  
Para modificar comportamiento de clases de Create sin extenderlas (e.g., `MixinRollerBlockEntity`, `MixinCarriageBogey`), Railway usa Mixins de SpongePowered/Architectury. Esto permite hookear en métodos privados.

**Patrón 3 — `MinRespectingScrollValueBehaviour`:**  
Railway creó una subclase de `ScrollValueBehaviour` que respeta un valor mínimo y permite valores negativos de forma más granular.

**Patrón 4 — MovementBehaviour para decoración animada:**  
Railway registra MovementBehaviours para chimeneas de locomotoras (`SmokeStackMovementBehaviour`) que emiten partículas mientras el tren está en movimiento.

**Patrón 5 — Datos compartidos del contraption:**  
`context.contraption.getStorage()` provee acceso al inventario y fluidos del contraption completo, útil para máquinas que procesan items mientras se mueven.

### F.5 Registrar tu Behaviour compartiendo el registro de Create

No necesitas un registro separado: `MovementBehaviour.REGISTRY`, `BlockStressValues.IMPACTS` y `BlockStressValues.CAPACITIES` son registros globales. Solo llama:

```java
// Desde tu FMLCommonSetupEvent:
MovementBehaviour.REGISTRY.register(MyBlocks.MY_BLOCK.get(), new MyBehaviour());
BlockStressValues.IMPACTS.register(MyBlocks.MY_MACHINE.get(), () -> 8.0);
```

---

## Sección G: AllTags relevantes

**Archivo:** `AllTags.java`

### G.1 AllBlockTags

| Tag | Namespace | Significado / Consecuencia de estar/no estar |
|-----|-----------|----------------------------------------------|
| `NON_MOVABLE` | `create:` | El bloque **no puede** montarse en contraptions. Create lo comprueba en `isMovementAllowed`. Si tu bloque debería ser inamovible (e.g., una máquina de escala industrial), ponlo aquí. |
| `BRITTLE` | `create:` | Se rompe (dropea como item) cuando la contraption intenta llevárselo. Útil para bloques frágiles que no deberían moverse pero tampoco quieres que bloqueen el contraption. |
| `SAFE_NBT` | `create:` | El NBT del BE se preserva al montar/desmontar del contraption. **Sin este tag**, el BE pierde sus datos. Si tu bloque tiene datos importantes (recetas cacheadas, inventario), agrégalo. |
| `CASING` | `create:` | El bloque es un "casing". Permite que bloques cinéticos se encapsulen en él para quitar el shaft visible. |
| `WRENCH_PICKUP` | `create:` | La llave inglesa puede recoger el bloque como item. |
| `WINDMILL_SAILS` | `create:` | Cuenta como vela de molino de viento. Puedes agregar bloques decorativos de tu addon aquí para que funcionen como velas. |
| `FAN_PROCESSING_CATALYSTS_BLASTING` | `create:` | El bloque puede actuar como catalizador de smelting para el Encased Fan. Agrega tu bloque "horno" aquí. |
| `FAN_PROCESSING_CATALYSTS_HAUNTING` | `create:` | Catalizador de haunting (soul fire). |
| `FAN_PROCESSING_CATALYSTS_SMOKING` | `create:` | Catalizador de smoking. |
| `FAN_PROCESSING_CATALYSTS_SPLASHING` | `create:` | Catalizador de washing (agua). |
| `PASSIVE_BOILER_HEATERS` | `create:` | El bloque puede calentar la caldera de forma pasiva. |
| `NON_BREAKABLE` | `create:` | El addon de ruptura de bloques (Block Breaker) no lo rompe. |
| `MOVABLE_EMPTY_COLLIDER` | `create:` | Tiene colisión incluso cuando está "vacío" durante el movimiento. |
| `SEATS` | `create:` | Es un asiento funcional en contraptions/trenes. |
| `TOOLBOXES` | `create:` | Toolbox compatible (se guarda en trenes, etc.). |
| `TRACKS` | `create:` | Es un carril de tren válido. |
| `SIMPLE_MOUNTED_STORAGE` | `create:` | Su inventario se monta automáticamente en contraptions como almacenamiento simple. |
| `SINGLE_BLOCK_INVENTORIES` | `create:` | Se trata como inventario de un solo bloque para identificación en packager. |
| `FAN_TRANSPARENT` | `create:` | El viento del Fan lo atraviesa. |
| `NON_HARVESTABLE` | `create:` | La cosechadora mecánica no lo cosecha. |

### G.2 AllItemTags

| Tag | Significado |
|-----|-------------|
| `CASING` | Items de casing (para recetas de encapsulado). |
| `CREATE_INGOTS` | Lingotes de Create (Andesite Alloy, Brass, Copper, etc.). |
| `CRUSHED_RAW_MATERIALS` | Items de minerales aplastados (Crushing Wheel output). |
| `SANDPAPER` | Papeles de lija (para secuencias de pulido). |
| `CONTRAPTION_CONTROLLED` | Items que el contraption puede usar/controlar. |
| `PRESSURIZED_AIR_SOURCES` | Fuentes de aire presurizado para cañones. |
| `DEPLOYABLE_DRINK` | Bebidas que el Deployer puede usar. |
| `SLEEPERS` | Traviesas de vía (Railway). |
| `PULPIFIABLE` | Items que se pueden hacer pulpa en el mixer con agua. |

### G.3 AllFluidTags

| Tag | Significado |
|-----|-------------|
| `BOTTOMLESS_ALLOW` | El fluid spout lo puede usar sin límite. |
| `BOTTOMLESS_DENY` | Prohibido explícitamente en modo bottomless. |

### G.4 AllEntityTags

| Tag | Significado |
|-----|-------------|
| `BLAZE_BURNER_CAPTURABLE` | La entidad puede ser capturada en el Blaze Burner. |
| `IGNORE_SEAT` | La entidad no se sienta automáticamente en asientos. |

### G.5 AllRecipeSerializerTags

| Tag | Significado |
|-----|-------------|
| `AUTOMATION_IGNORE` | Recetas que el automation (packager, etc.) ignora. |

---

## Sección H: Patrones reales de los addons existentes

### H.1 Cómo declaran dependencia de Create

**createaddition (Forge 1.20.1):**
```gradle
// build.gradle
dependencies {
    implementation fg.deobf("com.simibubi.create:create-${minecraft_version}:${create_version}:slim") { transitive = false }
    implementation fg.deobf("com.jozufozu.flywheel:flywheel-forge-${flywheel_mc_version}:${flywheel_version}")
}
```

**Steam 'n' Rails (Multiloader, 1.20.1):**
```kotlin
// build.gradle.kts — usa Architectury + multiloader setup
// Create se declara como dependencia en cada subproyecto (forge/fabric)
dependencies {
    common(project(":common", "namedElements"))
    // Create se incluye vía maven en forge.gradle.properties:
    // create_forge_version = 0.5.1.j-55
}
```

**Para NeoForge 1.21.1 (recomendado en tu addon):**
```gradle
dependencies {
    implementation "com.simibubi.create:create-neoforge-${mc_version}:${create_version}"
}
```

Además, en `mods.toml` / `neoforge.mods.toml`:
```toml
[[dependencies.mymod]]
    modId = "create"
    type = "required"
    versionRange = "[0.5.1,)"
    ordering = "AFTER"
    side = "BOTH"
```

### H.2 Patrón 1 — Extender el sistema kinético sin tocar Create internamente

Ambos addons evitan modificar clases de Create directamente. En su lugar:
- Extienden `KineticBlockEntity` o `GeneratingKineticBlockEntity`
- Usan los registros públicos (`BlockStressValues.IMPACTS`, `MovementBehaviour.REGISTRY`)
- Delegan en los métodos de `SmartBlockEntity` (`sendData()`, `setChanged()`)

### H.3 Patrón 2 — ScrollValueBehaviour para velocidad configurable

createaddition usa `KineticScrollValueBehaviour` (una subclase interna de Create para el Motor) para permitir que el jugador configure los RPM directamente en el bloque con la rueda del ratón. El addon llama `updateGeneratedRotation(newSpeed)` en el callback.

```java
// Patrón reproducible en tu addon:
generatedSpeed = new ScrollValueBehaviour(Component.translatable("mymod.speed"), this, slot);
generatedSpeed.between(-maxRpm, maxRpm);
generatedSpeed.withCallback(rpm -> updateGeneratedRotation((float) rpm));
behaviours.add(generatedSpeed);

@Override
public float getGeneratedSpeed() {
    return generatedSpeed.getValue();
}
```

### H.4 Patrón 3 — Mixins para hooks necesarios

Steam 'n' Rails usa Mixins cuando necesita modificar comportamiento profundo de Create (e.g., cómo se calculan las conexiones de Bogey, cómo se registran los tipos de vía). Esto es la última opción — primero verifica si la API pública (`BlockMovementChecks`, `ContraptionMovementSetting`, los registros) cubre tu caso.

### H.5 Patrón 4 — Compatibilidad condicional

createaddition comprueba la presencia de CC (ComputerCraft) y registra periféricos solo si está cargado:

```java
if (CreateAddition.CC_ACTIVE) {
    lazyPeripheral = LazyOptional.of(() -> Peripherals.createElectricMotorPeripheral(this));
}
```

En NeoForge 1.21.1, usa `ModList.get().isLoaded("computercraft")` y anota con `@OptionalInterface`.

### H.6 Patrón 5 — Datagen con SequencedAssemblyRecipeGen

Railway usa la API pública de Create para generar recetas de Sequenced Assembly de sus items:

```java
// sources/railway/.../RailwaysSequencedAssemblyRecipeGen.java
public class RailwaysSequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {
    // usa create("my_recipe", b -> b.require(...).addStep(...))
}
```

### H.7 Errores comunes identificados

Basado en el historial visible de los repos:

1. **createaddition** tuvo que ajustar `calculateAddedStressCapacity()` varias veces porque el valor de la config no se escala igual que el sistema interno de Create. Asegúrate de entender si Create espera "SU totales" o "SU por RPM" (es "SU base a 1 RPM" — se multiplica por `|speed|` automáticamente en la red).

2. **Steam 'n' Rails** tuvo conflictos con el renderizado de BEs dentro de contraptions. La solución fue implementar `ActorVisual` con Flywheel en lugar de usar el renderer estándar del BE.

3. Ambos addons tuvieron que actualizar sus registros de `MovementBehaviour` cuando Create migró de un sistema basado en `AllMovementBehaviours.registerBehaviour()` al `SimpleRegistry` en versiones posteriores. En 1.21.1, **usa siempre** `MovementBehaviour.REGISTRY.register(block, behaviour)` directamente.

---

## Commits de referencia

| Repositorio | Commit HEAD | Fecha |
|---|---|---|
| `Creators-of-Create/Create` rama `mc1.21.1/dev` | `32e0a2c` | 2026-05-19 |
| `mrh0/createaddition` | `85a819e` | (Forge 1.20.1 — rama más cercana disponible) |
| `Layers-of-Railways/Railway` | `5ef3a21e` | (Multiloader 1.20.1 — rama más cercana disponible) |

> Los commits de addons son de ramas 1.20.1 porque no existía rama 1.21.1 en ninguno de los dos repos al momento de generar este documento. Los patrones de código son transferibles; las APIs de NeoForge 1.21.1 (capabilities, registries) difieren en detalles de Forge 1.20.1.

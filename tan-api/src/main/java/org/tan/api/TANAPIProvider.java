package org.tan.api;

/**
 * Main entry point for accessing the Towns and Nations API.
 *
 * <p>This class provides static access to all API interfaces. The API implementations are
 * registered by the plugin on startup.
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * // Get a town by player
 * TownAPI townAPI = TANAPIProvider.getTownAPI();
 * TanTown town = townAPI.getTown(player);
 *
 * if (town != null) {
 *     player.sendMessage("You are in: " + town.getName());
 * }
 *
 * // Check economy balance
 * EconomyAPI economyAPI = TANAPIProvider.getEconomyAPI();
 * double balance = economyAPI.getBalance(player);
 * }</pre>
 *
 * <p><b>Note:</b> All API methods may return {@code null} if the requested data does not exist or
 * if the API has not been initialized yet. Always check for null returns.
 *
 * @since 0.1.0
 */
public class TANAPIProvider {

  private static TownAPI townAPI;
  private static EconomyAPI economyAPI;
  private static ClaimAPI claimAPI;

  /**
   * Get the TownAPI instance.
   *
   * @return The TownAPI instance.
   */
  public static TownAPI getTownAPI() {
    return townAPI;
  }

  /**
   * Get the EconomyAPI instance.
   *
   * @return The EconomyAPI instance.
   */
  public static EconomyAPI getEconomyAPI() {
    return economyAPI;
  }

  /**
   * Get the ClaimAPI instance.
   *
   * @return The ClaimAPI instance.
   */
  public static ClaimAPI getClaimAPI() {
    return claimAPI;
  }

  /**
   * Register the TownAPI implementation.
   *
   * @param townAPI The TownAPI implementation.
   */
  public static void registerTownAPI(TownAPI townAPI) {
    TANAPIProvider.townAPI = townAPI;
  }

  /**
   * Register the EconomyAPI implementation.
   *
   * @param economyAPI The EconomyAPI implementation.
   */
  public static void registerEconomyAPI(EconomyAPI economyAPI) {
    TANAPIProvider.economyAPI = economyAPI;
  }

  /**
   * Register the ClaimAPI implementation.
   *
   * @param claimAPI The ClaimAPI implementation.
   */
  public static void registerClaimAPI(ClaimAPI claimAPI) {
    TANAPIProvider.claimAPI = claimAPI;
  }
}

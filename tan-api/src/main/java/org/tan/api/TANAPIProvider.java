package org.tan.api;

/** Provides access to the Towns and Nations API. */
public class TANAPIProvider {

  private static TownAPI townAPI;
  private static NationAPI nationAPI;
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
   * Get the NationAPI instance.
   *
   * @return The NationAPI instance.
   */
  public static NationAPI getNationAPI() {
    return nationAPI;
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
   * Register the NationAPI implementation.
   *
   * @param nationAPI The NationAPI implementation.
   */
  public static void registerNationAPI(NationAPI nationAPI) {
    TANAPIProvider.nationAPI = nationAPI;
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
